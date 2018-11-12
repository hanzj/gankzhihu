package com.peakmain.gankzhihu.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Glide;
import com.peakmain.gankzhihu.R;
import com.peakmain.gankzhihu.bean.daily.Daily;
import com.peakmain.gankzhihu.bean.daily.HeadLine;
import com.peakmain.gankzhihu.bean.daily.Response;
import com.peakmain.gankzhihu.di.scope.ContextLife;
import com.peakmain.gankzhihu.ui.activity.DailyFeedActivity;
import com.peakmain.gankzhihu.ui.activity.GankWebActivity;
import com.peakmain.gankzhihu.utils.BannerUtils;
import com.peakmain.gankzhihu.utils.ScreenUtil;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ：Peakmain
 * version ：1.0
 * createTime ：2018/11/9 0009 下午 4:12
 * mail : 2726449200@qq.com
 * describe ：
 */
public class DailyListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private Response response;
    private int status = 1;
    private static final int TYPE_TOP = -1;
    private static final int TYPE_FOOTER = -2;
    private static final int TYPE_HEADLINE = -3;
    public static final int LOAD_MORE = 0;
    public static final int LOAD_PULL_TO = 1;
    public static final int LOAD_NONE = 2;
    public static final int LOAD_END = 3;
    private int feed_position;
    List<String> mBannerImage;
    List<String> mBannerTitle;

    @Inject
    public DailyListAdapter(@ContextLife Context context, Response response) {
        this.context = context;
        this.response = response;
    }

    @Override
    public int getItemViewType(int position) {
        if (response.getBanners() != null) {
            if (position == 0) {
                return TYPE_TOP;
            } else if (response.getHeadline().getPost() != null) {
                if (position == 1) {
                    return TYPE_HEADLINE;
                } else if (position + 1 == getItemCount()) {
                    return TYPE_FOOTER;
                } else {
                    feed_position = 2;//头和尾
                    return position;
                }
            } else if (position + 1 == getItemCount()) {
                return TYPE_FOOTER;
            } else {
                feed_position = 1;
                return position;
            }
        } else if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return position;
        }
    }

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof TopStoriesViewHolder) {
            TopStoriesViewHolder topStoriesViewHolder = (TopStoriesViewHolder) holder;
            topStoriesViewHolder.mBanner.startAutoPlay();
        }
    }

    @Override
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof TopStoriesViewHolder) {
            TopStoriesViewHolder topStoriesViewHolder = (TopStoriesViewHolder) holder;
            topStoriesViewHolder.mBanner.stopAutoPlay();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_TOP) {
            View rootView = View.inflate(parent.getContext(), R.layout.item_zhihu_top_stories, null);
            return new TopStoriesViewHolder(rootView);
        } else if (viewType == TYPE_HEADLINE) {
            View rootView = View.inflate(parent.getContext(), R.layout.item_daily_headline, null);
            return new HeadlineViewHolder(rootView);
        } else if (viewType == TYPE_FOOTER) {
            View view = View.inflate(parent.getContext(), R.layout.activity_view_footer, null);
            return new FooterViewHolder(view);
        } else {
            Daily daily = response.getFeeds().get(viewType - feed_position);
            switch (daily.getType()) {
                case 0: {
                    View rootView = View.inflate(parent.getContext(), R.layout.item_daily_feed_0, null);
                    return new Feed_0_ViewHolder(rootView);
                }
                case 1: {
                    View rootView = View.inflate(parent.getContext(), R.layout.item_daily_feed_1, null);
                    return new Feed_1_ViewHolder(rootView);
                }
                case 2: {
                    View rootView = View.inflate(parent.getContext(), R.layout.item_daily_feed_0, null);
                    return new Feed_0_ViewHolder(rootView);
                }
                default:
                    View rootView = View.inflate(parent.getContext(), R.layout.item_empty, null);
                    return new EmptyViewHolder(rootView);
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            FooterViewHolder footerViewHolder = (FooterViewHolder) holder;
            footerViewHolder.bindItem();
        } else if (holder instanceof TopStoriesViewHolder) {
            TopStoriesViewHolder topStoriesViewHolder = (TopStoriesViewHolder) holder;
            topStoriesViewHolder.bindItem(response.getBanners());
        } else if (holder instanceof HeadlineViewHolder) {
            HeadlineViewHolder headlineViewHolder = (HeadlineViewHolder) holder;
            headlineViewHolder.bindItem(response.getHeadline());
        } else if (holder instanceof Feed_1_ViewHolder) {
            Feed_1_ViewHolder feed_1_viewHolder = (Feed_1_ViewHolder) holder;
            Daily daily = response.getFeeds().get(position - feed_position);
            if (daily.getType() == 1) {
                feed_1_viewHolder.bindItem(daily);
            }
        } else if (holder instanceof Feed_0_ViewHolder) {
            Feed_0_ViewHolder feed_0_viewHolder = (Feed_0_ViewHolder) holder;
            Daily daily = response.getFeeds().get(position - feed_position);
            if (daily.getType() == 0) {
                feed_0_viewHolder.bindItem(daily);
            } else if (daily.getType() == 2) {
                feed_0_viewHolder.bindItem(daily);
            }
        } else if (holder instanceof EmptyViewHolder) {

        }
    }

    @Override
    public int getItemCount() {
        return response.getListSize() + 1;
    }

    /**
     * type = Empty
     */
    class EmptyViewHolder extends RecyclerView.ViewHolder {

        public EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * footer view
     */
    class FooterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_load_prompt)
        TextView tv_load_prompt;
        @BindView(R.id.progress)
        ProgressBar progress;

        public FooterViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            LinearLayoutCompat.LayoutParams params = new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ScreenUtil.instance(context).dip2px(40));
            itemView.setLayoutParams(params);
        }

        private void bindItem() {
            switch (status) {
                case LOAD_MORE:
                    progress.setVisibility(View.VISIBLE);
                    tv_load_prompt.setText("正在加载...");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_PULL_TO:
                    progress.setVisibility(View.GONE);
                    tv_load_prompt.setText("上拉加载更多");
                    itemView.setVisibility(View.VISIBLE);
                    break;
                case LOAD_NONE:
                    System.out.println("LOAD_NONE----");
                    progress.setVisibility(View.GONE);
                    tv_load_prompt.setText("已无更多加载");
                    break;
                case LOAD_END:
                    itemView.setVisibility(View.GONE);
                default:
                    break;
            }
        }
    }

    /**
     * TopStories
     */
    class TopStoriesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.vp_top_stories)
        Banner mBanner;
        @BindView(R.id.tv_tag)
        TextView tv_tag;

        public TopStoriesViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            tv_tag.setVisibility(View.GONE);
        }

        public void bindItem(List<Daily> banners) {
            mBannerImage = new ArrayList<>();
            mBannerTitle = new ArrayList<>();
            for (Daily d : banners) {
                mBannerImage.add(d.getImage());
                mBannerTitle.add(d.getPost().getTitle());
            }
            BannerUtils.initBanner(mBanner, mBannerImage, mBannerTitle);
            mBanner.setOnBannerListener(position -> {
                ARouter.getInstance().build("/activity/GankWebActivity")
                        .withString(GankWebActivity.GANK_URL,  banners.get(position).getPost().getAppview())
                        .navigation();
            });
        }
    }

    /**
     * headline
     */
    class HeadlineViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_headline_1)
        TextView tv_headline_1;
        @BindView(R.id.tv_headline_2)
        TextView tv_headline_2;
        @BindView(R.id.tv_headline_3)
        TextView tv_headline_3;
        @BindView(R.id.card_headline)
        CardView card_headline;

        public HeadlineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bindItem(Daily daily) {
            List<HeadLine> headLines = daily.getList();
            tv_headline_1.setText(headLines.get(0).getDescription());
            tv_headline_2.setText(headLines.get(1).getDescription());
            tv_headline_3.setText(headLines.get(2).getDescription());

            card_headline.setOnClickListener(v -> {
                ARouter.getInstance().build("/activity/GankWebActivity")
                        .withString(GankWebActivity.GANK_URL,  daily.getPost().getAppview())
                        .navigation();
            });
        }
    }

    /**
     * feed_0
     */
    class Feed_1_ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_feed_1_title)
        TextView tv_feed_1_title;
        @BindView(R.id.tv_feed_1_type)
        TextView tv_feed_1_type;
        @BindView(R.id.iv_feed_1_type_icon)
        ImageView iv_feed_1_type_icon;
        @BindView(R.id.iv_feed_1_icon)
        ImageView iv_feed_1_icon;
        @BindView(R.id.card_feed_1)
        CardView card_feed_1;

        public Feed_1_ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ScreenUtil screenUtil = ScreenUtil.instance(context);
            card_feed_1.setLayoutParams(new LinearLayout.LayoutParams(screenUtil.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public void bindItem(Daily daily) {
            tv_feed_1_title.setText(daily.getPost().getTitle());
            tv_feed_1_type.setText(daily.getPost().getCategory().getTitle());
            Glide.with(context).load(daily.getPost().getCategory().getImage_lab()).centerCrop().into(iv_feed_1_type_icon);
            Glide.with(context).load(daily.getImage()).centerCrop().into(iv_feed_1_icon);

            card_feed_1.setOnClickListener(v -> {
                ARouter.getInstance().build("/activity/GankWebActivity")
                        .withString(GankWebActivity.GANK_URL, daily.getPost().getAppview())
                        .navigation();
            });
        }
    }

    /**
     * feed_1
     */
    class Feed_0_ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.iv_feed_0_icon)
        ImageView iv_feed_0_icon;
        @BindView(R.id.tv_feed_0_title)
        TextView tv_feed_0_title;
        @BindView(R.id.tv_feed_0_desc)
        TextView tv_feed_0_desc;
        @BindView(R.id.iv_feed_0_type)
        ImageView iv_feed_0_type;
        @BindView(R.id.tv_Feed_0_type)
        TextView tv_Feed_0_type;
        @BindView(R.id.card_layout)
        CardView card_layout;

        public Feed_0_ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            ScreenUtil screenUtil = ScreenUtil.instance(context);
            card_layout.setLayoutParams(new LinearLayout.LayoutParams(screenUtil.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        }

        public void bindItem(Daily daily) {
            tv_feed_0_title.setText(daily.getPost().getTitle());
            tv_feed_0_desc.setText(daily.getPost().getDescription());
            tv_Feed_0_type.setText(daily.getPost().getCategory().getTitle());
            Glide.with(context).load(daily.getImage()).centerCrop().into(iv_feed_0_icon);
            if (daily.getType() == 0) {
                Glide.with(context).load(R.drawable.feed_0_icon).centerCrop().into(iv_feed_0_type);
                card_layout.setOnClickListener(v -> {
                    ARouter.getInstance().build("/activity/DailyFeedActivity")
                            .withString(DailyFeedActivity.FEED_ID, daily.getPost().getId())
                            .withString(DailyFeedActivity.FEED_DESC, daily.getPost().getDescription())
                            .withString(DailyFeedActivity.FEED_TITLE, daily.getPost().getTitle())
                            .withString(DailyFeedActivity.FEED_IMG, daily.getImage())
                            .navigation();
                });
            } else if (daily.getType() == 2) {
                Glide.with(context).load(R.drawable.feed_1_icon).centerCrop().into(iv_feed_0_type);
                card_layout.setOnClickListener(v -> {
                    ARouter.getInstance().build("/activity/GankWebActivity")
                            .withString(GankWebActivity.GANK_URL, daily.getPost().getAppview())
                            .navigation();
                });
            }


        }
    }

    // change recycler state
    public void updateLoadStatus(int status) {
        this.status = status;
        notifyDataSetChanged();
    }
}
