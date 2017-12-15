# Graywater: an android library for performant lists

Graywater is a [`RecyclerView`](https://developer.android.com/reference/android/support/v7/widget/RecyclerView.html) adapter that facilitates the performant decomposition of complex and varied list items. It does this by mapping large data models to multiple viewholders, splitting the work needed to create a complex list item over multiple frames.

The concept is based off of [Facebook's post on a faster news feed](https://code.facebook.com/posts/879498888759525/fast-rendering-news-feed-on-android/) and [Components for Android](https://code.facebook.com/posts/531104390396423/components-for-android-a-declarative-framework-for-efficient-uis/), which have been realized as [Litho](http://fblitho.com).

Tumblr developed Graywater to improve scroll performance, reduce memory usage, and lay the foundation for a more modular codebase.

The name "Graywater" comes from [the process of recycling water](https://en.wikipedia.org/wiki/Greywater).

* [What is it?](#what-is-it)
* [How do you use it?](#how-do-you-use-it)
* [How does it work?](#how-does-it-work)
* [Other features](#other-features)
* [An addendum on binders and generics](#an-addendum-on-binders-and-generics)

## What is it?

An adapter basically takes a list of models (of type `T`) and maps them to a list of viewholders (of type `VH extends RecyclerView.ViewHolder`).

One naive solution is to map models directly to viewholders. For example, a list of "posts" can have a viewholder for each post. But this architecture quickly becomes slow and unwieldy if there is either a large variety of posts or if individual posts are complex.

So to improve performance, the parts of a post that are offscreen can be recycled.

```
   model       views
+---------+   +------+ 
|         |   | head | <------- does not exist
|         |   +------+ <------------+
| item #1 |   | body | <---+        |
|         |   +------+     |        |
|         |   | foot |     |        |
+---------+   +------+     |        |
                           screen   view hierarchy
+---------+   +------+     |        |
|         |   | head |     |        |
|         |   +------+     |        |
| item #2 |   | body | <---+        |
|         |   +------+ <------------+
|         |   | body | <------- does not exist
+---------+   +------+
```

Due to Tumblr's needs, there are additional features that help improve performance and reduce memory usage:

* Viewholders are shared between models of the same and different types, _e.g. a body viewholder can be shared between a item #1 and item #2_.
* Models can have multiple viewholders of the same type, _e.g. an item can have an unlimited number of body viewholders_.

This results in a minimal number of viewholders to maximize cache effectiveness and reduce memory pressure.

In order to accomplish this, we introduce the concept of a **Binder**, which takes a model (`T`) and binds it to a viewholder (`VH`).

```
+-------+     +--------+     +------------+
| Model | --> | Binder | --> | ViewHolder |
+-------+     +--------+     +------------+
```

We no longer desire the one-to-one relationship between models and viewholders that, because monolithic models result in monolithic viewholders. For example, a video post (`VideoPost`) used to have a corresponding `VideoPostViewHolder`. Instead, we want `VideoPost` to be composed of a header, body, and footer.

```
                           +--------+     +------------+
                      /--> | Binder | --> | ViewHolder |
+-------+     +---+  /     +--------+     +------------+
| Model | --> | ? | *----> | Binder | --> | ViewHolder |
+-------+     +---+  \     +--------+     +------------+
                      \--> | Binder | --> | ViewHolder |
                           +--------+     +------------+
```

To manage this relationship, we introduce the concept of an **ItemBinder**, which aggregates the binders needed to display a post. It takes a model (`T`) and returns a list of binders, each of which bind the model to a specific viewholder.

```
                 +------------+
         /-----> | ItemBinder |
        /        +------------+
       /               v
      /            +--------+     +------------+
     /      /----> | Binder | --> | ViewHolder |
+-------+  /       +--------+     +------------+
| Model | *------> | Binder | --> | ViewHolder |
+-------+  \       +--------+     +------------+
            \----> | Binder | --> | ViewHolder |
                   +--------+     +------------+
```

* `ItemBinder<? extends T, ? extends VH>` takes a model `T` and maps it to a list of binders of type `Binder<T, ? extends VH>`.
* `Binder<? super T, ? extends VH>` takes a model of type `T` and maps it to a `ViewHolder` of type `VH`

A minor design point is that `RecyclerView.Adapter#onCreate()` creates the viewholders, so some sort of mechanism for creating viewholders is necessary. This is where **ViewHolderCreator** comes in - it is a model-independent way of creating viewholders (in other libraries with a one-to-one relationship between models and viewholders, this code would live in the model - e.g. [Epoxy](https://github.com/airbnb/epoxy#epoxy-models)).

```
                 +------------+
         /-----> | ItemBinder |
        /        +------------+
       /               v
      /            +--------+     +------------+     +-------------------+
     /      /----> | Binder | --> | ViewHolder | <-- | ViewHolderCreator |
+-------+  /       +--------+     +------------+     +-------------------+
| Model | *------> | Binder | --> | ViewHolder | <-- | ViewHolderCreator |
+-------+  \       +--------+     +------------+     +-------------------+
            \----> | Binder | --> | ViewHolder | <-- | ViewHolderCreator |
                   +--------+     +------------+     +-------------------+
```

### Dependency Injection with Dagger 2 Map Multibindings

For Graywater to know about the ItemBinders and ViewHolderCreators, each of them needs to be registered when the adapter is created. When there are a substantial number of both, there can be a significant impact on the time it takes to initialize the adapter.

One solution is to use [Dagger 2 map multibindings](https://google.github.io/dagger/multibindings#map-multibindings). This allows you to use the full power of dependency injection to control which binders a given screen will support, as well as the ability to inject different versions of the same binder on different screens to facilitate screen-dependent behavior.

```
                 +------------+                       +----------------+
         /-----> | ItemBinder | <-------------------- | Dagger 2 Maps  |
        /        +------------+                       +----------------+
       /               v                                       v
      /            +--------+     +------------+     +-------------------+
     /      /----> | Binder | --> | ViewHolder | <-- | ViewHolderCreator |
+-------+  /       +--------+     +------------+     +-------------------+
| Model | *------> | Binder | --> | ViewHolder | <-- | ViewHolderCreator |
+-------+  \       +--------+     +------------+     +-------------------+
            \----> | Binder | --> | ViewHolder | <-- | ViewHolderCreator |
                   +--------+     +------------+     +-------------------+
```

But using Dagger 2 by itself does not improve startup time, because the maps are created at injection time, which requires all the binders to also be created. This can be somewhat alleviated with `Lazy<Map>`, but another benefit of Dagger 2 is the automatic support for `Map<K, Provider<V>>`. When applied to `ItemBinders`, this allows each `ItemBinder` to be constructed on demand.

_Note that Graywater does not have built-in support for Dagger 2._

### Lazy Loading Binders

Normally, when an item is added to the adapter, the corresponding `ItemBinder` is loaded as well as all the necessary `Binder` classes.

```
 Binders             ItemBinders                Items             Screen  
+--------+          +------------+          +-----------+       +--------+
| Photo  | -------- |            |       /- | TextPost  |       | Header |
+--------+    /---- | Photo Post | -\   /   +-----------+       +--------+
| Footer | --x /--- |            |   \----- | PhotoPost |       |        |
+--------+    x     +------------+    /     +-----------+       |        |
| Header | --x \--- |            | --/   /- | TextPost  |       | Text   |
+--------+    \---- | Text Post  |      /   +-----------+       |        |
| Text   | -------- |            | ----/                        |        |
+--------+          +------------+                              +--------+
```

But on-screen, only the first item is visible, and out of the first item, only two components are visible. So in the above example, there is no need to load the "Footer" binder. This is what `List<Provider<Binder>>` facilitates.

```
 Binders             ItemBinders                Items             Screen  
+--------+          +------------+          +-----------+       +--------+
| Photo  |          |            |      /-- | TextPost  | -x--- | Header |
+--------+          | Photo Post |     /    +-----------+   \   +--------+
| Footer |          |            |    /     | PhotoPost |    \- |        |
+--------+          +------------+   /      +-----------+       |        |
| Header | ---\     |            | -/       | TextPost  |       | Text   |
+--------+     \--- | Text Post  |          +-----------+       |        |
| Text   | -------- |            |                              |        |
+--------+          +------------+                              +--------+
```

This is very useful for improving initialization performance when loading long cached lists by deferring binder creation until the binder is nearly on screen.

## How do you use it?

Graywater relies heavily on generics for type safety - here are the major type parameters:

  * `T` is the base model type.
  * `VH` is the base viewholder type.
  * `MT` is the type of the model type (e.g. `Class<?>`).

Although this may seem overly generic, it is convenient if your base model or viewholder type has methods you need to access.

Add a model that subclasses `T`.

```java
class Text {
  String text;
}
```

Create the viewholder(s).

```java
class TextViewHolder extends RecyclerView.ViewHolder {

  TextView textView;

  public TextViewHolder(View view) {
    super(view);
    textView = (TextView) view.findViewById(R.id.text);
  }
}
```

Create the corresponding `ViewHolderCreator` implementations.

```java
class TextViewHolderCreator implements GraywaterAdapter.ViewHolderCreator {

  public TextViewHolder create(final ViewGroup parent) {
    return new TextViewHolder(GraywaterAdapter.inflate(parent, R.layout.item_text));
  }

  public int getViewType() {
    return R.layout.item_text;
  }
}
```

Create the `Binder<T, ? extends VH>` implementations for each `ViewHolder`.

```java
class TextBinder implements GraywaterAdapter.Binder<Text, TextViewHolder> {

  public Class<TextViewHolder> getViewHolderType() {
    return TextViewHolder.class;
  }

  public void prepare(final Text model, 
                      final List<GraywaterAdapter.Binder<? super Text, ? extends TextViewHolder>> binders, 
                      final int binderIndex) {
    
  }

  public void bind(final Text model, 
                   final TextViewHolder holder, 
                   final List<GraywaterAdapter.Binder<? super Text, ? extends TextViewHolder>> binders, 
                   final int binderIndex, 
                   final GraywaterAdapter.ActionListener<Text, TextViewHolder> actionListener) {
    holder.textView.setText(model.text);
  }

  public void unbind(final TextViewHolder holder) {
    holder.textView.setText(null);
  }
}
```

Create the `ItemBinder` that returns the list of binders for the model.

```java
class TextItemBinder implements GraywaterAdapter.ItemBinder<Text, RecyclerView.ViewHolder> {
    
  TextBinder textBinder;

  public TextItemBinder(TextBinder textBinder) {
    this.textBinder = textBinder;
  }

  public List<GraywaterAdapter.Binder<? super Text, ? extends RecyclerView.ViewHolder>> getBinderList(
      final Text model, 
      final int position) {
    return new ArrayList<GraywaterAdapter.Binder<? super Text, ? extends RecyclerView.ViewHolder>>() {{
      add(textBinder);
      add(textBinder);
    }};
  }
}
```

Lastly, subclass `GraywaterAdapter` and register the created classes!

```java
private static class TextAdapter extends GraywaterAdapter<Text, RecyclerView.ViewHolder, Class<?>> {

  public TextAdapter() {
    register(new TextViewHolderCreator(), TextViewHolder.class);

    final TextBinder textBinder = new TextBinder();

    register(String.class, new TextItemBinder(textBinder), null);
  }

  @Override
  protected Class<?> getModelType(final Text model) {
    return model.getClass();
  }
}
```

You can then add items using `GraywaterAdapter.add()` or remove them with `GraywaterAdapter.remove()`. Note that `getItemCount()` will return the number of viewholders, not the number of model objects in your list. `getModelType(MT)` will generally have the example implementation, but it may be useful to have a custom implementation if subtypes have different definitions across types, or if you need a "default" type.

In example code, an adapter is created that repeats each item in the list once.

## How does it work?

At its core, Graywater maps models to viewholders, which basically means it is a just a dictionary. These are the fields used in a dictionary-like way:

* `List<T> mItems` - the list of items (or a map of position to item)
* `Map<Class<? extends VH>, ViewHolderCreator> mViewHolderCreatorMap` - the map of viewholder class to `ViewHolderCreator`.
* `Map<MT, ItemBinder<? extends T, ? extends VH>> mItemBinderMap` - the map of `MT` (model type) to `ItemBinder`
* `Map<MT, ActionListener<? extends T, ? extends VH>> mActionListenerMap` - the map of `MT` (model type) to `ActionListener`

So in `add()`, the new model is added to the list of items. In `register()`, the parameters are added to the respective map.

A simple optimization is to cache the ItemBinders. This is done by `binderListCache`, which is of type `List<List<Binder<? super T, ? extends VH>>>`. Every time `add()` is called, `getBinderList()` is called and the return value is added to the cache.

What is `MT`?

```java
protected abstract MT getModelType(T model);
```

Instead of automatically using the class of the model as the model's type, it can be anything (preferably a similar property of the model).

Note that `RecyclerView.Adapter` has these methods:

```java
abstract class Adapter<VH extends ViewHolder> {
  abstract VH onCreateViewHolder(ViewGroup parent, int viewType);
  abstract void onBindViewHolder(VH holder, int position);
  int getItemViewType(int position);
  abstract int getItemCount();
}
```

It is important to note that `position` in the above methods is the _viewholder_ position, not the _model_ position. This distinction is extremely important, because when we are given the _viewholder_ position when we need the _model_ position.

For now, we assume that `viewType` has a one-to-one correspondence to the viewholder class.

Here is a visualization of the model and viewholder positions:

```
 model      viewholder
 position   position

 +-----+    +-----+
 |     |    |  0  |
 |     |    +-----+
 |     |    |  1  |
 |  0  |    +-----+
 |     |    |  2  |
 |     |    +-----+
 |     |    |  3  |
 +-----+    +-----+

 +-----+    +-----+
 |     |    |  4  |
 |  1  |    +-----+
 |     |    |  5  |
 +-----+    +-----+
```

If we are given a viewholder position of `5`, we need to arrive at the model position of `1`, that way we can grab the model from the backing data store.

The way to do this is to iterate through the models, going to the corresponding `ItemBinder` and accumulating the size of the list that is returned. Unfortunately, this is slow.

But in order to make it fast, we need to cache a lot of intermediary state.
On `add()`, we compute these two caches, `viewHolderToItemPosition` and `itemPositionToFirstViewHolderPosition`. Note that the code uses _item_ to refer to the _model_ position.

```
 model      viewholder    viewHolderToItemPos   itemPosToFirstViewHolderPos
 position   position

 +-----+    +-----+
 |     |    |  0  |           { 0, 0 }
 |     |    +-----+
 |     |    |  1  |           { 1, 0 }
 |  0  |    +-----+                                    { 0, 0 }
 |     |    |  2  |           { 2, 0 }
 |     |    +-----+
 |     |    |  3  |           { 3, 0 }
 +-----+    +-----+

 +-----+    +-----+
 |     |    |  4  |           { 4, 1 }
 |  1  |    +-----+                                    { 1, 4 }
 |     |    |  5  |           { 5, 1 }
 +-----+    +-----+
```

`viewHolderToItemPositionCache` is also used for `getItemCount()`.

`itemPositionToFirstViewHolderPosition` is primarily used for one purpose: to determine the position of the viewholder and associated binder in the list of viewholders _for a given model_. In the above example, the viewholder at position `5` is the 2nd viewholder for the 2nd model. This is important when there is more than one instance of a viewholder for a model, such as reblog comments.

`getItemViewType()` works by tracking the registered `ViewHolderCreators`, which have this interface:

```java
interface ViewHolderCreator {
  RecyclerView.ViewHolder create(ViewGroup parent);
  int getViewType();
}
```

When a new `ViewHolderCreator` is registered, it is added to `viewHolderCreatorList`, which is of type `SparseArray<Class<? extends VH>>`, and associates the `viewType` with the correct class. The class is then associated with the `ViewHolderCreator` via `viewHolderCreatorMap`, which is of type `Map<Class<? extends VH>, ViewHolderCreator>`.

```
     viewHolderCreatorList                     viewHolderCreatorMap
+------------------------------+     +---------------------------------------+
| viewtype -> ViewHolder.class |     | ViewHolder.class -> ViewHolderCreator |
+==============================+     +=======================================+
|  8324    -> Header.class     |     |  Header.class    -> HeaderCreator     |
+------------------------------+     +---------------------------------------+
|  9802    -> Body.class       |     |  Body.class      -> BodyCreator       |
+------------------------------+     +---------------------------------------+
|  2383    -> Footer.class     |     |  Footer.class    -> FooterCreator     |
+------------------------------+     +---------------------------------------+
```

To implement `getItemViewType()` So when given a _viewholder_ position, 

1. `viewHolderToItemPos` is used to retrieve the model position.
2. `binderListCache` and the model position is used to get the list of binders.
3. `itemPosToFirstViewHolderPos` is used to retrieve the position of the first viewholder.
4. The binder position in the list of binders is computed.
5. The correct binder for the viewholder position is retrieved.
6. `viewHolderCreatorMap` is passed the `Binder.getViewHolderType()` to get the `ViewHolderCreator`.
7. `ViewHolderCreator.getViewType()` is called to get the `viewType`.

`onCreateViewHolder(ViewGroup parent, int viewType)` is quite a bit simpler:

```java
return (VH) viewHolderCreatorMap.get(getViewHolderClass(viewType)).create(parent);
```

1. `viewHolderCreatorList` is used to get the class from the `viewType`
2. `viewHolderCreatorMap` is used to get the `ViewHolderCreator`
3. The `ViewHolderCreator` creates the new viewholder.

`onBindViewHolder(VH holder, int viewHolderPosition)` is implemented by following the first 5 steps of `getItemViewType()`, and then calling `Binder.bind()` with the model and the viewholder.

## Other features

In `bind()`, the adapter looks ahead to the next `numViewHoldersToPrepare()` viewholders (default 3) and calls `Binder.prepare()` on them. Note that it does not call `prepare()` more than once, unless `unbind()` is called. This state is stored in `viewHolderPreparedCache` which stores the indices of viewholders that have been prepared.

This also works in both directions of the `RecyclerView`. It checks the order of `bind()` operations to determine which direction to prepare viewholders in.

`ActionListener` is a bit of an experimental feature to avoid creating extra `ClickListener` objects on every `bind()` call.

## An addendum on binders and generics

`ItemBinder#getBinderList()` has a somewhat complex return type:

```java
List<Binder<? super T, ? extends VH>> getBinderList(@NonNull T model, int position)
```

In particular, `Binder<? super T, ? extends VH>` is quite confusing.

When you write your binder, try to parameterize the binder with the least-restrictive model it can take and the most restrictive viewholder it can bind to.

If these were your type hierarchies:

```
 Model Type              ViewHolder Type
 Hierarchy               Hierarchy 

     A                        1    
   /   \    <- Binder ->    /   \  
  B     C                  2     3 
```

* A binder should be written to take `A` if possible, `B` or `C` if necessary.
* A binder can only be written to take `2` or `3` (note that `1` should never be registered, because then it is ambiguous).

This can be illustrated with an example

```
      Post                     BaseViewHolder
     /    \      <- Binder ->      /    \    
  Text    Photo                Header   Body 
```

If every post has a header, it makes sense to have

* `HeaderBinder extends Binder<Post, Header>`
* `PhotoBinder extends Binder<Photo, Body>`
* `TextBinder extends Binder<Text, Body>`

That way `HeaderBinder` can take a `Text` or a `Photo`, while `PhotoBinder` won't ever take a `Text`.

So what does the `ItemBinder<T, VH>` look like?

* `TextItemBinder extends ItemBinder<Text, BaseViewHolder>`
  - `HeaderBinder`
  - `TextBinder`
* `PhotoItemBinder extends ItemBinder<Photo, BaseViewHolder>`
  - `HeaderBinder`
  - `PhotoBinder`

You can see that `HeaderBinder` binds `Post`, which is a superclass of `Text`, while `TextBinder` binds to `Body`, which is a subclass of `BaseViewHolder`. 

When registering:

* `register(Text.class, textItemBinder)`
* `register(Photo.class, photoItemBInder)`

assuming the class is the model type.

The adapter should be of type `GraywaterAdapter<Post, BaseViewHolder>`, the superclasses for all the types used.

## Contact

* [Eric Leong](mailto:ericleong@tumblr.com)

## License

Copyright 2017 Tumblr, Inc.

Licensed under the Apache License, Version 2.0 (the “License”); you may not use
this file except in compliance with the License. You may obtain a copy of the
License at [apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0).

> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an “AS IS” BASIS, WITHOUT
> WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
> License for the specific language governing permissions and limitations under
> the License.
