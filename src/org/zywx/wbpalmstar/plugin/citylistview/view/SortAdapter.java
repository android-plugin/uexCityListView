package org.zywx.wbpalmstar.plugin.citylistview.view;

import java.util.List;
import java.util.Locale;

import org.zywx.wbpalmstar.engine.universalex.EUExUtil;
import org.zywx.wbpalmstar.plugin.citylistview.ImageColorUtils;
import org.zywx.wbpalmstar.plugin.citylistview.ListViewBean;
import org.zywx.wbpalmstar.plugin.citylistview.view.PinnedHeaderListView.PinnedHeaderAdapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class SortAdapter extends BaseAdapter implements SectionIndexer,
		OnScrollListener, PinnedHeaderAdapter {
	private List<SortModel> list = null;
	private boolean isSearchMode = false;
	private Context mContext;
	private SortOnClickListener softOnClickListener;
	private boolean isHiddenImgTitle;
	private PinnedHeaderListView mPinnedHeaderListView;
	private ListViewBean listViewBean;

	public SortAdapter(Context mContext, List<SortModel> list,
			PinnedHeaderListView pinnedHeaderListView) {
		this.mContext = mContext;
		this.list = list;
		this.mPinnedHeaderListView = pinnedHeaderListView;
	}

	/**
	 * 当ListView数据发生变化时,调用此方法来更新ListView
	 * 
	 * @param list
	 */
	public void updateListView(List<SortModel> list, boolean isSearchMode) {
		this.list = list;
		this.isSearchMode = isSearchMode;
		notifyDataSetChanged();
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		if (position < list.size()) {
			return list.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		final SortModel mContent = list.get(position);
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					EUExUtil.getResLayoutID("plugin_uexcitylistview_listitem_layout"), null);
			viewHolder.tvTitle = (TextView) view.findViewById(EUExUtil.getResIdID("title"));
			viewHolder.tvLetter = (TextView) view.findViewById(EUExUtil.getResIdID("catalog"));
			viewHolder.lineView = view.findViewById(EUExUtil.getResIdID("lineView"));
			viewHolder.itemLinearLayout = (LinearLayout) view
					.findViewById(EUExUtil.getResIdID("itemLinearLayout"));
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		if (listViewBean != null) {
			if (!TextUtils.isEmpty(listViewBean.getSeparatorLineColor())) {
				viewHolder.lineView.setBackgroundColor(ImageColorUtils
						.parseColor(listViewBean.getSeparatorLineColor()));
			}
			if (!TextUtils.isEmpty(listViewBean.getSectionHeaderTitleColor())) {
				viewHolder.tvLetter.setTextColor(ImageColorUtils
						.parseColor(listViewBean.getSectionHeaderTitleColor()));
			}
			if (!TextUtils.isEmpty(listViewBean.getSectionHeaderBgColor())) {
				viewHolder.tvLetter.setBackgroundColor(ImageColorUtils
						.parseColor(listViewBean.getSectionHeaderBgColor()));
			}
			if (!TextUtils.isEmpty(listViewBean.getItemTextColor())) {
				viewHolder.tvTitle.setTextColor(ImageColorUtils
						.parseColor(listViewBean.getItemTextColor()));
			}
			if(!TextUtils.isEmpty(listViewBean.getBgColor())){
				viewHolder.itemLinearLayout.setBackgroundColor(ImageColorUtils
						.parseColor(listViewBean.getBgColor()));
			}
		}
		if (isHiddenImgTitle) {
			viewHolder.tvLetter.setVisibility(View.GONE);
			viewHolder.lineView.setVisibility(View.VISIBLE);
		} else {
			if (mContent.isHiddenLine()) {
				viewHolder.lineView.setVisibility(View.GONE);
			} else {
				viewHolder.lineView.setVisibility(View.VISIBLE);
			}
			// 根据position获取分类的首字母的Char ascii值
			int section = getSectionForPosition(position);
			// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
			if (position == getPositionForSection(section)
					&& needTitle(position)) {
				if (viewHolder.tvLetter.getVisibility() != View.VISIBLE) {
					viewHolder.tvLetter.setVisibility(View.VISIBLE);
				}
				viewHolder.tvLetter.setText(mContent.getSortLetters());
			} else {
				if (viewHolder.tvLetter.getVisibility() != View.GONE) {
					viewHolder.tvLetter.setVisibility(View.GONE);
				}
			}
		}
		viewHolder.tvTitle.setText(mContent.getName());
		viewHolder.itemLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (softOnClickListener != null)
					softOnClickListener.onClick(view, position);
			}
		});
		return view;

	}
	
	public interface SortOnClickListener {
		public void onClick(View view, int position);
	}
	
	final static class ViewHolder {
		TextView tvLetter;
		TextView tvTitle;
		View lineView;
		LinearLayout itemLinearLayout;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return list.get(position).getSortLetters().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		int count = getCount();
		for (int i = 0; i < count; i++) {
			String sortStr = list.get(i).getSortLetters();
			char firstChar = sortStr.toUpperCase(Locale.US).charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public Object[] getSections() {
		return null;
	}

	public void setSoftOnClickListener(SortOnClickListener softOnClickListener) {
		this.softOnClickListener = softOnClickListener;
	}

	@Override
	public int getPinnedHeaderState(int position) {
		if (getCount() == 0 || position < 0) {
			return PinnedHeaderAdapter.PINNED_HEADER_GONE;
		}

		if (isMove(position) == true) {
			return PinnedHeaderAdapter.PINNED_HEADER_PUSHED_UP;
		}

		return PinnedHeaderAdapter.PINNED_HEADER_VISIBLE;
	}

	@Override
	public void configurePinnedHeader(View headerView, int position, int alpaha) {
		// 设置标题的内容
		SortModel itemEntity = (SortModel) getItem(position);
		String headerValue = itemEntity.getSortLetters();
		if (!TextUtils.isEmpty(headerValue)) {
			TextView headerTextView = (TextView) headerView
					.findViewById(EUExUtil.getResIdID("header"));
			headerTextView.setText(headerValue);
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (view instanceof PinnedHeaderListView) {
			((PinnedHeaderListView) view)
					.controlPinnedHeader(firstVisibleItem - 1);
			if (firstVisibleItem != 0 && !isSearchMode) {
				mPinnedHeaderListView.setPinnedHeaderHidden(false);
			}
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
	}

	private boolean isMove(int position) {
		if (position >= getCount()) {
			return false;
		}
		// 获取当前与下一项
		SortModel currentEntity = (SortModel) getItem(position);
		SortModel nextEntity = (SortModel) getItem(position + 1);
		if (null == currentEntity || null == nextEntity) {
			return false;
		}

		// 获取两项header内容
		String currentTitle = currentEntity.getSortLetters();
		String nextTitle = nextEntity.getSortLetters();
		if (null == currentTitle || null == nextTitle) {
			return false;
		}

		// 当前不等于下一项header，当前项需要移动了
		if (!currentTitle.equals(nextTitle)) {
			return true;
		}
		return false;
	}

	/**
	 * 判断是否需要显示标题
	 * 
	 * @param position
	 * @return
	 */
	private boolean needTitle(int position) {
		// 第一个肯定是分类
		if (position == 0) {
			return true;
		}

		// 异常处理
		if (position < 0) {
			return false;
		}

		// 当前 // 上一个
		SortModel currentEntity = (SortModel) getItem(position);
		SortModel previousEntity = (SortModel) getItem(position - 1);
		if (null == currentEntity || null == previousEntity) {
			return false;
		}

		String currentTitle = currentEntity.getSortLetters();
		String previousTitle = previousEntity.getSortLetters();
		if (null == previousTitle || null == currentTitle) {
			return false;
		}

		// 当前item分类名和上一个item分类名不同，则表示两item属于不同分类
		if (currentTitle.equals(previousTitle)) {
			return false;
		}

		return true;
	}

	public void setHiddenImgTitle(boolean isHiddenImgTitle) {
		this.isHiddenImgTitle = isHiddenImgTitle;
	}

	public void setListViewBean(ListViewBean listViewBean) {
		this.listViewBean = listViewBean;
	}
}