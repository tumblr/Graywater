package com.tumblr.graywater;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * To work on unit tests, switch the Test Artifact in the Build Variants view.
 */
@RunWith(RobolectricTestRunner.class)
public class GraywaterAdapterTest {

	private static class TestAdapter extends GraywaterAdapter<Object, RecyclerView.ViewHolder, Class<?>> {

		@Override
		protected Class<?> getModelType(final Object model) {

			Class<?> modelType = model.getClass();

			// Types are messy.
			final ItemBinder itemBinder = mItemBinderMap.get(modelType);

			final Class<?> declaringClass = modelType.getDeclaringClass();
			if (itemBinder == null && declaringClass != null) {
				modelType = declaringClass;
			}

			return modelType;
		}

		private static class TextViewHolder extends RecyclerView.ViewHolder {
			public TextViewHolder(final View itemView) {
				super(itemView);
			}
		}

		private static class ImageViewHolder extends RecyclerView.ViewHolder {
			public ImageViewHolder(final View itemView) {
				super(itemView);
			}
		}

		private static class TextViewHolderCreator implements GraywaterAdapter.ViewHolderCreator {

			public static final int VIEW_TYPE = 1;

			@Override
			public TextViewHolder create(final ViewGroup parent) {
				return new TextViewHolder(new TextView(parent.getContext()));
			}

			@Override
			public int getViewType() {
				return VIEW_TYPE;
			}
		}

		private static class ImageViewHolderCreator implements GraywaterAdapter.ViewHolderCreator {

			public static final int VIEW_TYPE = 2;

			@Override
			public ImageViewHolder create(final ViewGroup parent) {
				return new ImageViewHolder(new ImageView(parent.getContext()));
			}

			@Override
			public int getViewType() {
				return VIEW_TYPE;
			}
		}

		private static class TextBinder implements Binder<String, TextViewHolder> {

			@NonNull
			@Override
			public Class<TextViewHolder> getViewHolderType() {
				return TextViewHolder.class;
			}

			@Override
			public void prepare(@NonNull final String model,
			                    final List<Binder<? super String, ? extends TextViewHolder>> binderList, final int binderIndex) {

			}

			@Override
			public void bind(@NonNull final String model, @NonNull final TextViewHolder holder,
			                 @NonNull final List<Binder<? super String, ? extends TextViewHolder>> binders,
			                 final int binderIndex,
			                 @NonNull final ActionListener<String, TextViewHolder> actionListener) {
				((TextView) holder.itemView).setText(model);
			}

			@Override
			public void unbind(@NonNull final TextViewHolder holder) {
				((TextView) holder.itemView).setText("");
			}
		}

		private static class ImageBinder implements Binder<Uri, ImageViewHolder> {
			@NonNull
			@Override
			public Class<ImageViewHolder> getViewHolderType() {
				return ImageViewHolder.class;
			}

			@Override
			public void prepare(@NonNull final Uri model,
			                    final List<Binder<? super Uri, ? extends ImageViewHolder>> binderList,
			                    final int binderIndex) {

			}

			@Override
			public void bind(@NonNull final Uri model,
			                 @NonNull final ImageViewHolder holder,
			                 @NonNull final List<Binder<? super Uri, ? extends ImageViewHolder>> binders,
			                 final int binderIndex,
			                 @NonNull final ActionListener<Uri, ImageViewHolder> actionListener) {
				((ImageView) holder.itemView).setImageURI(model); // not a good idea in production ;)
			}

			@Override
			public void unbind(@NonNull final ImageViewHolder holder) {

			}
		}

		public TestAdapter() {
			super();

			register(new TextViewHolderCreator(), TextViewHolder.class);
			register(new ImageViewHolderCreator(), ImageViewHolder.class);

			final TextBinder textBinder = new TextBinder();
			final ImageBinder imageBinder = new ImageBinder();

			register(String.class, new ItemBinder<String, RecyclerView.ViewHolder>() {
				@NonNull
				@Override
				public List<Binder<? super String, ? extends RecyclerView.ViewHolder>> getBinderList(@NonNull final String model,
				                                                                                     final int position) {
					return new ArrayList<Binder<? super String, ? extends RecyclerView.ViewHolder>>() {{
						add(textBinder);
						add(textBinder);
					}};
				}
			}, null);

			register(Uri.class, new ItemBinder<Uri, RecyclerView.ViewHolder>() {
				@NonNull
				@Override
				public List<Binder<? super Uri, ? extends RecyclerView.ViewHolder>> getBinderList(@NonNull final Uri model,
				                                                                                  final int position) {
					return new ArrayList<Binder<? super Uri, ? extends RecyclerView.ViewHolder>>() {{
						add(imageBinder);
						add(imageBinder);
						add(imageBinder);
					}};
				}
			}, null);
		}
	}

	@Test
	public void testAdd() throws Exception {
		final TestAdapter adapter = new TestAdapter();

		adapter.add("one");
		assertEquals(2, adapter.getItemCount());

		adapter.add("two");
		assertEquals(4, adapter.getItemCount());

		adapter.add("three");
		assertEquals(6, adapter.getItemCount());
	}

	@Test
	public void testRemove() throws Exception {
		final TestAdapter adapter = new TestAdapter();

		adapter.add("zero");
		adapter.add("one");
		adapter.add("two");
		adapter.add("three");
		adapter.add("four");
		adapter.add("five");

		// remove
		adapter.remove(0);
		assertEquals(2 * 5, adapter.getItemCount());
		adapter.remove(4);
		assertEquals(2 * 4, adapter.getItemCount());
		adapter.remove(2);
		assertEquals(2 * 3, adapter.getItemCount());

		// state
		final List<Object> items = adapter.getItems();
		assertEquals(3, items.size());
		assertEquals("one", items.get(0));
		assertEquals("two", items.get(1));
		assertEquals("four", items.get(2));
	}

	@Test
	public void testViewHolderPosition() throws Exception {
		final TestAdapter adapter = new TestAdapter();

		adapter.add("zero");
		adapter.add("one");
		adapter.add("two");
		adapter.add("three");
		adapter.add("four");
		adapter.add("five");

		// ["zero", "zero", "one", "one, ... ]
		final GraywaterAdapter.BinderResult one = adapter.computeItemAndBinderIndex(2);
		assertEquals(1, one.itemPosition);
		assertEquals(0, one.binderIndex);
		assertEquals("one", one.item);

		final GraywaterAdapter.BinderResult oneDouble = adapter.computeItemAndBinderIndex(3);
		assertEquals(1, oneDouble.itemPosition);
		assertEquals(1, oneDouble.binderIndex);
		assertEquals("one", oneDouble.item);

		final GraywaterAdapter.BinderResult three = adapter.computeItemAndBinderIndex(6);
		assertEquals(3, three.itemPosition);
		assertEquals(0, three.binderIndex);
		assertEquals("three", three.item);

		final GraywaterAdapter.BinderResult threeDouble = adapter.computeItemAndBinderIndex(7);
		assertEquals(3, threeDouble.itemPosition);
		assertEquals(1, threeDouble.binderIndex);
		assertEquals("three", three.item);

		final GraywaterAdapter.BinderResult zero = adapter.computeItemAndBinderIndex(0);
		assertEquals(0, zero.itemPosition);
		assertEquals(0, zero.binderIndex);
		assertEquals("zero", zero.item);

		final GraywaterAdapter.BinderResult five = adapter.computeItemAndBinderIndex(11);
		assertEquals(5, five.itemPosition);
		assertEquals(1, five.binderIndex);
		assertEquals("five", five.item);
	}

	@Test
	public void testViewHolderPositionWithRemove() throws Exception {
		final TestAdapter adapter = new TestAdapter();

		adapter.add("zero");
		adapter.add("one");
		adapter.add("two");
		adapter.add("three");
		adapter.add("four");
		adapter.add("five");

		adapter.remove(0);

		// ["one", "one, "two", "two", ... ]
		final GraywaterAdapter.BinderResult two = adapter.computeItemAndBinderIndex(2);
		assertEquals(1, two.itemPosition);
		assertEquals(0, two.binderIndex);

		// four is in position five
		final GraywaterAdapter.BinderResult four = adapter.computeItemAndBinderIndex(9);
		assertEquals(4, four.itemPosition);
		assertEquals(1, four.binderIndex);
	}

	@Test
	public void testViewHolderPositionWithRemoveThenAdd() throws Exception {
		final TestAdapter adapter = new TestAdapter();

		adapter.add("zero");
		adapter.add("one");
		adapter.add("two");
		adapter.add("three");
		adapter.add("four");
		adapter.add("five");

		adapter.remove(0);

		adapter.add(0, "zero");

		// ["zero", "zero", "one", "one, ... ]
		final GraywaterAdapter.BinderResult one = adapter.computeItemAndBinderIndex(2);
		assertEquals(1, one.itemPosition);
		assertEquals(0, one.binderIndex);
		assertEquals("one", one.item);

		final GraywaterAdapter.BinderResult oneDouble = adapter.computeItemAndBinderIndex(3);
		assertEquals(1, oneDouble.itemPosition);
		assertEquals(1, oneDouble.binderIndex);
		assertEquals("one", oneDouble.item);

		final GraywaterAdapter.BinderResult three = adapter.computeItemAndBinderIndex(6);
		assertEquals(3, three.itemPosition);
		assertEquals(0, three.binderIndex);
		assertEquals("three", three.item);

		final GraywaterAdapter.BinderResult threeDouble = adapter.computeItemAndBinderIndex(7);
		assertEquals(3, threeDouble.itemPosition);
		assertEquals(1, threeDouble.binderIndex);
		assertEquals("three", three.item);

		final GraywaterAdapter.BinderResult zero = adapter.computeItemAndBinderIndex(0);
		assertEquals(0, zero.itemPosition);
		assertEquals(0, zero.binderIndex);
		assertEquals("zero", zero.item);

		final GraywaterAdapter.BinderResult five = adapter.computeItemAndBinderIndex(11);
		assertEquals(5, five.itemPosition);
		assertEquals(1, five.binderIndex);
		assertEquals("five", five.item);
	}

	@Test
	public void testViewHolderPositionWithRemoveThenAddMiddle() throws Exception {
		final TestAdapter adapter = new TestAdapter();

		adapter.add("zero");
		adapter.add("one");
		adapter.add("two");
		adapter.add("three");
		adapter.add("four");
		adapter.add("five");

		final Object obj = adapter.remove(2);
		assertEquals("two", obj);

		adapter.add(2, "two");

		// ["zero", "zero", "one", "one, ... ]
		final GraywaterAdapter.BinderResult one = adapter.computeItemAndBinderIndex(2);
		assertEquals(1, one.itemPosition);
		assertEquals(0, one.binderIndex);
		assertEquals("one", one.item);

		final GraywaterAdapter.BinderResult oneDouble = adapter.computeItemAndBinderIndex(3);
		assertEquals(1, oneDouble.itemPosition);
		assertEquals(1, oneDouble.binderIndex);
		assertEquals("one", oneDouble.item);

		final GraywaterAdapter.BinderResult three = adapter.computeItemAndBinderIndex(6);
		assertEquals(3, three.itemPosition);
		assertEquals(0, three.binderIndex);
		assertEquals("three", three.item);

		final GraywaterAdapter.BinderResult threeDouble = adapter.computeItemAndBinderIndex(7);
		assertEquals(3, threeDouble.itemPosition);
		assertEquals(1, threeDouble.binderIndex);
		assertEquals("three", three.item);

		final GraywaterAdapter.BinderResult zero = adapter.computeItemAndBinderIndex(0);
		assertEquals(0, zero.itemPosition);
		assertEquals(0, zero.binderIndex);
		assertEquals("zero", zero.item);

		final GraywaterAdapter.BinderResult five = adapter.computeItemAndBinderIndex(11);
		assertEquals(5, five.itemPosition);
		assertEquals(1, five.binderIndex);
		assertEquals("five", five.item);
	}

	@Test
	public void testGetItemViewType() throws Exception {
		final TestAdapter adapter = new TestAdapter();

		// ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
		//  "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
		adapter.add(Uri.parse("https://www.tumblr.com"));
		assertEquals(3, adapter.getItemCount());
		adapter.add("one");
		assertEquals(5, adapter.getItemCount());
		adapter.add(Uri.parse("http://dreamynomad.com"));
		assertEquals(8, adapter.getItemCount());
		adapter.add("three");
		assertEquals(10, adapter.getItemCount());
		adapter.add(Uri.parse("https://google.com"));
		assertEquals(13, adapter.getItemCount());
		adapter.add("five");
		assertEquals(15, adapter.getItemCount());

		assertEquals(TestAdapter.ImageViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(0));
		assertEquals(TestAdapter.ImageViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(1));
		assertEquals(TestAdapter.ImageViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(2));
		assertEquals(TestAdapter.TextViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(3));
		assertEquals(TestAdapter.TextViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(4));
		assertEquals(TestAdapter.ImageViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(5));
		assertEquals(TestAdapter.ImageViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(6));
		// ...
		assertEquals(TestAdapter.ImageViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(10));
		assertEquals(TestAdapter.ImageViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(11));
		assertEquals(TestAdapter.ImageViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(12));
		assertEquals(TestAdapter.TextViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(13));
		assertEquals(TestAdapter.TextViewHolderCreator.VIEW_TYPE, adapter.getItemViewType(14));
	}

	@Test
	public void testMultiViewHolderPosition() throws Exception {
		final TestAdapter adapter = new TestAdapter();

		// ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
		//  "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
		final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
		adapter.add(tumblrUri);
		assertEquals(3, adapter.getItemCount());
		adapter.add("one");
		assertEquals(5, adapter.getItemCount());
		adapter.add(Uri.parse("http://dreamynomad.com"));
		assertEquals(8, adapter.getItemCount());
		adapter.add("three");
		assertEquals(10, adapter.getItemCount());
		final Uri googleUri = Uri.parse("https://google.com");
		adapter.add(googleUri);
		assertEquals(13, adapter.getItemCount());
		adapter.add("five");
		assertEquals(15, adapter.getItemCount());

		final GraywaterAdapter.BinderResult tumblr = adapter.computeItemAndBinderIndex(1);
		assertEquals(0, tumblr.itemPosition);
		assertEquals(1, tumblr.binderIndex);
		assertEquals(tumblrUri, tumblr.item);

		final GraywaterAdapter.BinderResult one = adapter.computeItemAndBinderIndex(3);
		assertEquals(1, one.itemPosition);
		assertEquals(0, one.binderIndex);
		assertEquals("one", one.item);

		final GraywaterAdapter.BinderResult google = adapter.computeItemAndBinderIndex(12);
		assertEquals(4, google.itemPosition);
		assertEquals(2, google.binderIndex);
		assertEquals(googleUri, google.item);

		final GraywaterAdapter.BinderResult five = adapter.computeItemAndBinderIndex(14);
		assertEquals(5, five.itemPosition);
		assertEquals(1, five.binderIndex);
		assertEquals("five", five.item);
	}

	@Test
	public void testMultiViewHolderPositionWithRemoveThenAdd() throws Exception {
		final TestAdapter adapter = new TestAdapter();

		// ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
		//  "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
		final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
		adapter.add(tumblrUri);
		assertEquals(3, adapter.getItemCount());
		adapter.add("one");
		assertEquals(5, adapter.getItemCount());
		adapter.add(Uri.parse("http://dreamynomad.com"));
		assertEquals(8, adapter.getItemCount());
		adapter.add("three");
		assertEquals(10, adapter.getItemCount());
		final Uri googleUri = Uri.parse("https://google.com");
		adapter.add(googleUri);
		assertEquals(13, adapter.getItemCount());
		adapter.add("five");
		assertEquals(15, adapter.getItemCount());

		adapter.remove(3);
		adapter.add(3, "three");

		final GraywaterAdapter.BinderResult tumblr = adapter.computeItemAndBinderIndex(1);
		assertEquals(0, tumblr.itemPosition);
		assertEquals(1, tumblr.binderIndex);
		assertEquals(tumblrUri, tumblr.item);

		final GraywaterAdapter.BinderResult one = adapter.computeItemAndBinderIndex(3);
		assertEquals(1, one.itemPosition);
		assertEquals(0, one.binderIndex);
		assertEquals("one", one.item);

		final GraywaterAdapter.BinderResult google = adapter.computeItemAndBinderIndex(12);
		assertEquals(4, google.itemPosition);
		assertEquals(2, google.binderIndex);
		assertEquals(googleUri, google.item);

		final GraywaterAdapter.BinderResult five = adapter.computeItemAndBinderIndex(14);
		assertEquals(5, five.itemPosition);
		assertEquals(1, five.binderIndex);
		assertEquals("five", five.item);
	}

	@Test
	public void testClear() throws Exception {
		final TestAdapter adapter = new TestAdapter();

		// ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
		//  "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
		final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
		adapter.add(tumblrUri);
		assertEquals(3, adapter.getItemCount());
		adapter.add("one");
		assertEquals(5, adapter.getItemCount());
		adapter.add(Uri.parse("http://dreamynomad.com"));
		assertEquals(8, adapter.getItemCount());
		adapter.add("three");
		assertEquals(10, adapter.getItemCount());
		final Uri googleUri = Uri.parse("https://google.com");
		adapter.add(googleUri);
		assertEquals(13, adapter.getItemCount());
		adapter.add("five");
		assertEquals(15, adapter.getItemCount());

		adapter.clear();

		assertEquals(0, adapter.getItemCount());
	}

	@Test
	public void testClearThenAdd() throws Exception {
		final TestAdapter adapter = new TestAdapter();

		// ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
		//  "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
		final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
		adapter.add(tumblrUri);
		assertEquals(3, adapter.getItemCount());
		adapter.add("one");
		assertEquals(5, adapter.getItemCount());
		adapter.add(Uri.parse("http://dreamynomad.com"));
		assertEquals(8, adapter.getItemCount());
		adapter.add("three");
		assertEquals(10, adapter.getItemCount());
		final Uri googleUri = Uri.parse("https://google.com");
		adapter.add(googleUri);
		assertEquals(13, adapter.getItemCount());
		adapter.add("five");
		assertEquals(15, adapter.getItemCount());

		// Clear!
		adapter.clear();

		// ["https://www.tumblr.com", "https://www.tumblr.com", "https://www.tumblr.com", "one", "one",
		//  "http://dreamynomad.com", "http://dreamynomad.com", "http://dreamynomad.com", ...]
		adapter.add(tumblrUri);
		assertEquals(3, adapter.getItemCount());
		adapter.add("one");
		assertEquals(5, adapter.getItemCount());
		adapter.add(Uri.parse("http://dreamynomad.com"));
		assertEquals(8, adapter.getItemCount());
		adapter.add("three");
		assertEquals(10, adapter.getItemCount());
		adapter.add(googleUri);
		assertEquals(13, adapter.getItemCount());
		adapter.add("five");
		assertEquals(15, adapter.getItemCount());

		final GraywaterAdapter.BinderResult tumblr = adapter.computeItemAndBinderIndex(1);
		assertEquals(0, tumblr.itemPosition);
		assertEquals(1, tumblr.binderIndex);
		assertEquals(tumblrUri, tumblr.item);

		final GraywaterAdapter.BinderResult one = adapter.computeItemAndBinderIndex(3);
		assertEquals(1, one.itemPosition);
		assertEquals(0, one.binderIndex);
		assertEquals("one", one.item);

		final GraywaterAdapter.BinderResult google = adapter.computeItemAndBinderIndex(12);
		assertEquals(4, google.itemPosition);
		assertEquals(2, google.binderIndex);
		assertEquals(googleUri, google.item);

		final GraywaterAdapter.BinderResult five = adapter.computeItemAndBinderIndex(14);
		assertEquals(5, five.itemPosition);
		assertEquals(1, five.binderIndex);
		assertEquals("five", five.item);
	}

	@Test
	public void GraywaterAdapter_FirstVHPosition_FoundVHPosition() throws Exception {
		final TestAdapter adapter = new TestAdapter();
		adapter.add("Testing");
		final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
		adapter.add(tumblrUri);
		adapter.add("Testing");
		final int imageViewHolderPosition = adapter.getFirstViewHolderPosition(1, TestAdapter.ImageViewHolder.class);
		assertEquals(2, imageViewHolderPosition);
	}

	@Test
	public void GraywaterAdapter_FirstVHPosition_DidNotFindVHPosition() throws Exception {
		final TestAdapter adapter = new TestAdapter();
		adapter.add("Testing");
		final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
		adapter.add(tumblrUri);
		adapter.add("Testing");
		final int imageViewHolderPosition = adapter.getFirstViewHolderPosition(0, TestAdapter.ImageViewHolder.class);
		assertEquals(-1, imageViewHolderPosition);
	}

	@Test
	public void GraywaterAdapter_FirstVHPosition_InvalidItemPosition() throws Exception {
		final TestAdapter adapter = new TestAdapter();
		adapter.add("Testing");
		final Uri tumblrUri = Uri.parse("https://www.tumblr.com");
		adapter.add(tumblrUri);
		adapter.add("Testing");
		final int imageViewHolderPosition = adapter.getFirstViewHolderPosition(0xDEADBEEF, TestAdapter.ImageViewHolder.class);
		assertEquals(-1, imageViewHolderPosition);
	}
}
