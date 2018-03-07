package com.huiyun.amnews.view;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.huiyun.amnews.R;
import com.huiyun.amnews.util.PhoneUtils;
import com.huiyun.amnews.util.ViewPageScrollAnim;

import java.util.ArrayList;
import java.util.List;

/**
 * 轮播图
 *
 * @author Little Roy
 */
public class LoopViewPager extends LinearLayout implements
        OnPageChangeListener, Runnable {
    private Context context;
    private ViewPager viewPager;
    private List<View> pageViews;
    private ImageView[] imageViews;
    private String[] pageTexts;
    private boolean isContinue;
    private int screenWidth;
    private TextView pageTextView;
    private RelativeLayout pagerLayout;
    private int prePic;
    private LinearLayout group;
    // private Thread thread;
    private MyViewPagerAdapter adapter;
    private OnPagerClickLisenter pagerLisenter;
    private int scrollSeconds = 6;
    private List<String> imageList;
    private int selectCicle, unselectCicle;
    private Thread thread;
    public static final int POINT_LEFT = 0;
    public static final int POINT_CENTER = 1;
    public static final int POINT_RIGHT = 2;
    public static final int IMAGE_FITXY =0;
    public static final int IMAGE_CENTER_CROP = 1;

    public LoopViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initPageView();
    }

    private void initView() {
        screenWidth = PhoneUtils.getScreenWidth(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        inflater.inflate(R.layout.loop_viewpage, this);
        pageTextView = (TextView) findViewById(R.id.video_page_text);
        pagerLayout = (RelativeLayout) findViewById(R.id.view_pager_content);
        group = (LinearLayout) findViewById(R.id.viewGroup);
        viewPager = new ViewPager(context);
        pageViews = new ArrayList<>();
        pagerLayout.addView(viewPager);
        if (thread == null) {
            thread = new Thread(this);
            thread.start();
        }

    }

    // 初始化viewpage
    public void initPageView() {
        initView();
        imageList = new ArrayList<>();
        adapter = new MyViewPagerAdapter(context, pageViews);
        viewPager.setAdapter(adapter);
    }

    /**
     * 显示控件，给控件提供数据
     *
     * @param imageUrlList 图片地址
     * @param titleList    标题
     * @param isShowTitle  是否显示标题
     */
    public void initPageView(List<String> imageUrlList, List<String> titleList,
                             boolean isShowTitle,int bg) {
        if (imageUrlList.isEmpty() || (isShowTitle && titleList.isEmpty())) {
            return;
        }

        this.imageList.removeAll(this.imageList);
        if (this.imageList.size() > 0) {
            this.imageList.removeAll(imageList);
        }
        pageViews.removeAll(pageViews);
        adapter.notifyDataSetChanged();

        pagerLayout.removeAllViews();
        if (imageUrlList.size() == 1) {
            ImageView img = new ImageView(context);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            img.setLayoutParams(lp);
            img.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            GlideImageUtil.load(context, imageUrlList.get(0), img, bg,  bg);
//            LoadLocalImageUtil.getInstance().displayImageForNet(imageUrlList.get(0), img);
            Glide.with(context)
                    .load(imageUrlList.get(0))
                    .placeholder(R.drawable.vr_default_img)
                    .dontAnimate()
                    .into(img);
            img.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (pagerLisenter != null)
                        pagerLisenter.onPagerClickLisenter(0);
                }
            });
            pagerLayout.addView(img);
            return;
        }
        initPageView();
        isContinue = false;
        for (String s : imageUrlList) {
            this.imageList.add(s);
        }

        if (!TextUtils.isEmpty(imageList.get(imageList.size() - 1))) {
            ImageView lastImage = new ImageView(context);
            lastImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            GlideImageUtil.load(context, imageList.get(imageList.size() - 1), lastImage, bg, bg);
//            LoadLocalImageUtil.getInstance().displayImageForNet(imageList.get(imageList.size() - 1), lastImage);
            Glide.with(context)
                    .load(imageList.get(imageList.size() - 1))
                    .placeholder(R.drawable.vr_default_img)
                    .dontAnimate()
                    .into(lastImage);
            pageViews.add(lastImage);
        }
        for (int i = 0; i < imageList.size(); i++) {
            ImageView imageView = new ImageView(context);
            if (imageList.get(i) != null && !imageList.get(i).equals("")) {
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                GlideImageUtil.load(context, imageList.get(i), imageView,bg, bg);
//                LoadLocalImageUtil.getInstance().displayImageForNet(imageList.get(i), imageView);
                Glide.with(context)
                        .load(imageList.get(i))
                        .placeholder(R.drawable.vr_default_img)
                        .dontAnimate()
                        .into(imageView);
            }
            pageViews.add(imageView);
        }
        if (!TextUtils.isEmpty(imageList.get(0))) {
            ImageView firstImage = new ImageView(context);
            firstImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            GlideImageUtil.load(context, imageList.get(0), firstImage, bg, bg);
//            LoadLocalImageUtil.getInstance().displayImageForNet(imageList.get(0), firstImage);
            Glide.with(context)
                    .load(imageList.get(0))
                    .placeholder(R.drawable.vr_default_img)
                    .dontAnimate()
                    .into(firstImage);
            pageViews.add(firstImage);
        }
        if (isShowTitle) {
            pageTextView.setVisibility(View.VISIBLE);
            if (titleList != null && titleList.size() > 0) {
                pageTexts = new String[titleList.size()];
                for (int i = 0; i < titleList.size(); i++) {
                    pageTexts[i] = titleList.get(i);
                }
                pageTextView.setText(pageTexts[0]);
            }
        } else {
            pageTextView.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
        prePic = 1;
        viewPager.setCurrentItem(prePic);
        initCirclePoint();

        if (isShowTitle && !titleList.isEmpty()) {
            if (pageTexts.length > 0) {
                String text = pageTexts[0];
                text = TextUtils.isEmpty(text) ? " " : text;
                pageTextView.setText(text);
            }

        }
        viewPager.addOnPageChangeListener(this);
        ViewPageScrollAnim.controlViewPagerSpeed(viewPager);
        isContinue = true;

    }

    public void setPointPosition(int pointPosition) {
        if (pointPosition == POINT_LEFT)
            group.setGravity(Gravity.LEFT);
        else if (pointPosition == POINT_CENTER)
            group.setGravity(Gravity.CENTER);
        else
            group.setGravity(Gravity.RIGHT);
    }

    public void setLooperViewPagerHeight(int dp) {
        pagerLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                screenWidth, PhoneUtils.dip2px(context, dp)));
    }

    /**
     * 高度占屏幕宽度的比例
     *
     * @param scale
     */
    public void setLooperViewPagerScale(int scale) {
        pagerLayout.setLayoutParams(new RelativeLayout.LayoutParams(
                screenWidth, screenWidth / scale));
    }

    private void initCirclePoint() {
        group.removeAllViews();
        imageViews = new ImageView[pageViews.size() - 2];
        // 广告栏的小圆点图标
        for (int i = 0; i < pageViews.size() - 2; i++) {
            // 创建一个ImageView, 并设置宽高. 将该对象放入到数组中
            ImageView imageView = new ImageView(context);
            LayoutParams p;
            if (screenWidth < 720) {
                p = new LayoutParams(10, 10);
                p.setMargins(5, 0, 5, 0);
            } else {
                p = new LayoutParams(18, 18);
                p.setMargins(10, 0, 10, 0);
            }
            imageView.setLayoutParams(p);
            imageView.setPadding(5, 0, 5, 0);
            imageViews[i] = imageView;
            selectCicle = R.drawable.dot_selected;
            unselectCicle = R.drawable.dot_unselected;
            // 初始值, 默认第0个选中
            if (i == 0) {
                imageViews[i].setBackgroundResource(selectCicle);
            } else {
                imageViews[i].setBackgroundResource(unselectCicle);
            }
            // 将小圆点放入到布局中
            group.addView(imageViews[i]);
        }
    }


    /*
     * 每隔固定时间切换广告栏图片
     */
    private final Handler viewHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                viewPager.setCurrentItem(1, false);
                prePic = 2;
            } else if (viewPager.getCurrentItem() == 0) {
                viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 1,
                        false);
                prePic = 1;
            }
            viewPager.setCurrentItem(prePic);

        }
    };

    @Override
    public void onPageScrollStateChanged(int arg0) {
        switch (arg0) {
            case 1:
                isContinue = false;
                scrollSeconds = 5;
                // 如果是滑动到最后一个item 则把viewpager设置到第一个item 也就是第一张图片
                if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                    viewPager.setCurrentItem(1, false);
                    // 如果是滑动到第一个item 则把viewpage设置为最后一个item 也就是最后一张图片
                } else if (viewPager.getCurrentItem() == 0) {
                    viewPager.setCurrentItem(viewPager.getAdapter().getCount() - 2,
                            false);
                }
                break;

            case 2:
                isContinue = true;
                break;
        }
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        if (pageViews.size() < 2)
            return;
        if (arg0 == pageViews.size() - 1) {
            for (int i = 0; i < imageViews.length; i++) {
                if (i == 0) {
                    imageViews[i].setBackgroundResource(selectCicle);
                } else {
                    imageViews[i].setBackgroundResource(unselectCicle);
                }
            }
        } else if (arg0 == 0) {
            for (int i = 0; i < imageViews.length; i++) {
                if (i == imageViews.length - 1) {
                    imageViews[i].setBackgroundResource(selectCicle);
                } else {
                    imageViews[i].setBackgroundResource(unselectCicle);
                }
            }
        } else {
            for (int i = 0; i < imageViews.length; i++) {
                if (arg0 - 1 == i) {
                    imageViews[i].setBackgroundResource(selectCicle);
                } else {
                    imageViews[i].setBackgroundResource(unselectCicle);
                }
            }
        }
        prePic = arg0;
        if ((View.VISIBLE == pageTextView.getVisibility())
                && (pageTexts.length > 0)) {
            if (viewPager.getCurrentItem() == viewPager.getAdapter().getCount() - 1) {
                pageTextView.setText(pageTexts[0]);
            } else if (viewPager.getCurrentItem() == 0) {
                pageTextView.setText(pageTexts[pageTexts.length - 1]);
            } else {
                pageTextView.setText(pageTexts[arg0 - 1]);
            }
        }
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private List<View> views = null;

        /**
         * 初始化数据源, 即View数组
         */
        public MyViewPagerAdapter(Context context, List<View> views) {
            this.views = views;
        }

        /**
         * 从ViewPager中删除集合中对应索引的View对象
         */
        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(views.get(position));
        }

        /**
         * 获取ViewPager的个数
         */
        @Override
        public int getCount() {
            return views.size();
        }

        /**
         * 从View集合中获取对应索引的元素, 并添加到ViewPager中
         */
        @Override
        public Object instantiateItem(final View container, final int position) {
            ((ViewPager) container).addView(views.get(position), 0);
            views.get(position).setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (imageList == null || imageList.size() == 0)
                        return;

                    int clickPosition = 0;
                    if (position == views.size() - 1) {
                        clickPosition = 0;
                    } else if (position == 0) {
                        clickPosition = imageList.size() - 1;
                    } else {
                        clickPosition = position - 1;
                    }
                    if (pagerLisenter != null)
                        pagerLisenter.onPagerClickLisenter(clickPosition);


                }
            });
            return views.get(position);
        }

        /**
         * 是否将显示的ViewPager页面与instantiateItem返回的对象进行关联 这个方法是必须实现的
         */
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    // 点击监听
    public void setOnPagerClickLisenter(OnPagerClickLisenter pagerLisenter) {
        if (pagerLisenter != null) {
            this.pagerLisenter = pagerLisenter;
        }
    }

    // 点击回调接口
    public interface OnPagerClickLisenter {
        void onPagerClickLisenter(int clickPosition);
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            try {
                if (isContinue && imageList.size() > 1) {
                    if (scrollSeconds == 0) {
                        if (prePic == pageViews.size() - 1) {
                            prePic = 0;
                        } else {
                            prePic++;
                        }
                        viewHandler.sendEmptyMessage(prePic);
                        scrollSeconds = 5;
                    }
                }
                Thread.sleep(1000);
                if (isContinue)
                    scrollSeconds--;
            } catch (InterruptedException e) {

            }
        }
    }


}