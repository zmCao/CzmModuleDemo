package com.czm.module.common.widget.wheel;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.czm.module.common.dialogfragment.DialogUtil;
import com.czm.module.common.utils.DateUtil;
import com.czm.module.common.utils.StringUtils;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class WheelUtil {

	/**
	 * 描述：默认的年月日的日期选择器.
	 *
	 * @param mContext
	 * @param mText the m text
	 * @param iTextSize 字体大小SP
	 * @param iCenterSelectDrawablefinal 选中覆盖背景
	 * @param mWheelViewY  选择年的轮子
	 * @param mWheelViewM  选择月的轮子
	 * @param mWheelViewD  选择日的轮子
	 * @param okBtn 确定按钮
	 * @param cancelBtn 取消按钮
	 * @param defaultYear  默认显示的年
	 * @param defaultMonth the default month
	 * @param defaultDay the default day
	 * @param startYear    开始的年
	 * @param endYearOffset 结束的年与开始的年的偏移
	 * @param initStart  轮子是否初始化默认当前日期
	 */
	public static void initWheelDatePicker(final Context mContext, final TextView mText, int iTextSize, int iCenterSelectDrawablefinal, final WheelView mWheelViewY, final WheelView mWheelViewM, final WheelView mWheelViewD,
                                           Button okBtn, Button cancelBtn,
                                           int defaultYear, int defaultMonth, int defaultDay, final int startYear, int endYearOffset, boolean initStart){

		int endYear = startYear+endYearOffset;
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };
		//时间选择可以这样实现
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);

		if(initStart){
			defaultYear = year;
			defaultMonth = month;
			defaultDay = day;
		}

//		mText.setText(StrUtil.dateTimeFormat(defaultYear+"-"+defaultMonth+"-"+defaultDay));
		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		//设置"年"的显示数据
		mWheelViewY.setAdapter(new NumericWheelAdapter(startYear, endYear));
		mWheelViewY.setLabel("年");  // 添加文字
		mWheelViewY.setTextSize(iTextSize);
		mWheelViewY.setItemHeight(20);
		mWheelViewY.setCurrentItem(defaultYear - startYear);// 初始化时显示的数据
		mWheelViewY.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 月
		mWheelViewM.setAdapter(new NumericWheelAdapter(1, 12));
		mWheelViewM.setLabel("月");
		mWheelViewM.setTextSize(iTextSize);
		mWheelViewM.setItemHeight(20);
		mWheelViewM.setCurrentItem(defaultMonth-1);
		mWheelViewM.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 日
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(defaultMonth))) {
			mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(defaultMonth))) {
			mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if (DateUtil.isLeapYear(year)){
				mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
			}else{
				mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
			}
		}
		mWheelViewD.setLabel("日");
		mWheelViewD.setTextSize(iTextSize);
		mWheelViewD.setItemHeight(20);
		mWheelViewD.setCurrentItem(defaultDay - 1);
		mWheelViewD.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {

			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + startYear;
				int mDIndex = mWheelViewM.getCurrentItem();
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(mWheelViewM.getCurrentItem() + 1))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(mWheelViewM.getCurrentItem() + 1))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (DateUtil.isLeapYear(year_num))
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
					else
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
				}
				mWheelViewM.setCurrentItem(mDIndex);

			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {

			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					int year_num = mWheelViewY.getCurrentItem() + startYear;
					if (DateUtil.isLeapYear(year_num))
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
					else
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
				}
				mWheelViewD.setCurrentItem(0);
			}
		};
		mWheelViewY.addChangingListener(wheelListener_year);
		mWheelViewM.addChangingListener(wheelListener_month);

		if(okBtn!=null) {
			okBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DialogUtil.removeDialog(v);
					int indexYear = mWheelViewY.getCurrentItem();
					String year = mWheelViewY.getAdapter().getItem(indexYear);

					int indexMonth = mWheelViewM.getCurrentItem();
					String month = mWheelViewM.getAdapter().getItem(indexMonth);

					int indexDay = mWheelViewD.getCurrentItem();
					String day = mWheelViewD.getAdapter().getItem(indexDay);

					mText.setText(DateUtil.dateTimeFormat(year + "-" + month + "-" + day));
				}

			});
		}

		if(cancelBtn!=null) {
			cancelBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					DialogUtil.removeDialog(v);
				}
			});
		}
	}


	/**
	 * 描述：默认的年月日的日期选择器.（确定事件在外部处理）
	 *
	 * @param mContext
	 * @param iTextSize 字体大小SP
	 * @param iCenterSelectDrawablefinal 选中覆盖背景
	 * @param mWheelViewY  选择年的轮子
	 * @param mWheelViewM  选择月的轮子
	 * @param mWheelViewD  选择日的轮子
	 * @param cancelBtn 取消按钮
	 * @param defaultYear  默认显示的年
	 * @param defaultMonth the default month
	 * @param defaultDay the default day
	 * @param startYear    开始的年
	 * @param endYearOffset 结束的年与开始的年的偏移
	 * @param initStart  轮子是否初始化默认当前日期
	 */
	public static void initWheelDatePicker2(final Context mContext, int iTextSize, int iCenterSelectDrawablefinal, final WheelView mWheelViewY, final WheelView mWheelViewM, final WheelView mWheelViewD,
                                            Button cancelBtn, int defaultYear, int defaultMonth, int defaultDay, final int startYear, int endYearOffset, boolean initStart){

		int endYear = startYear+endYearOffset;
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };
		//时间选择可以这样实现
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);

		if(initStart){
			defaultYear = year;
			defaultMonth = month;
			defaultDay = day;
		}

//		mText.setText(StrUtil.dateTimeFormat(defaultYear+"-"+defaultMonth+"-"+defaultDay));
		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		//设置"年"的显示数据
		mWheelViewY.setAdapter(new NumericWheelAdapter(startYear, endYear));
		mWheelViewY.setLabel("年");  // 添加文字
		mWheelViewY.setTextSize(iTextSize);
		mWheelViewY.setItemHeight(20);
		mWheelViewY.setCurrentItem(defaultYear - startYear);// 初始化时显示的数据
		mWheelViewY.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 月
		mWheelViewM.setAdapter(new NumericWheelAdapter(1, 12));
		mWheelViewM.setLabel("月");
		mWheelViewM.setTextSize(iTextSize);
		mWheelViewM.setItemHeight(20);
		mWheelViewM.setCurrentItem(defaultMonth-1);
		mWheelViewM.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 日
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(defaultMonth))) {
			mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(defaultMonth))) {
			mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if (DateUtil.isLeapYear(year)){
				mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
			}else{
				mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
			}
		}
		mWheelViewD.setLabel("日");
		mWheelViewD.setTextSize(iTextSize);
		mWheelViewD.setItemHeight(20);
		mWheelViewD.setCurrentItem(defaultDay - 1);
		mWheelViewD.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {

			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + startYear;
				int mDIndex = mWheelViewM.getCurrentItem();
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(mWheelViewM.getCurrentItem() + 1))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(mWheelViewM.getCurrentItem() + 1))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (DateUtil.isLeapYear(year_num))
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
					else
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
				}
				mWheelViewM.setCurrentItem(mDIndex);

			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {

			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					int year_num = mWheelViewY.getCurrentItem() + startYear;
					if (DateUtil.isLeapYear(year_num))
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
					else
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
				}
				mWheelViewD.setCurrentItem(0);
			}
		};
		mWheelViewY.addChangingListener(wheelListener_year);
		mWheelViewM.addChangingListener(wheelListener_month);
		if(cancelBtn!=null) {
			cancelBtn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					DialogUtil.removeDialog(v);
				}
			});
		}
	}


	/**
	 * 描述：默认的时分的时间选择器.
	 *
	 * @param mContext
	 * @param mText the m text
	 * @param iTextSize 字体大小SP
	 * @param iCenterSelectDrawablefinal 选中覆盖背景
	 * @param mWheelViewHH the m wheel view hh
	 * @param mWheelViewMM  选择分的轮子
	 * @param okBtn 确定按钮
	 * @param cancelBtn 取消按钮
	 * @param defaultHour the default hour
	 * @param defaultMinute the default minute
	 * @param initStart
	 */
	public static void initWheelTimePicker2(final Context mContext, final TextView mText, int iTextSize, int iCenterSelectDrawablefinal, final WheelView mWheelViewHH, final WheelView mWheelViewMM,
                                            Button okBtn, Button cancelBtn,
                                            int defaultHour, int defaultMinute, boolean initStart){
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		if(initStart){
			defaultHour = hour;
			defaultMinute = minute;
		}

		// 时
		mWheelViewHH.setAdapter(new NumericWheelAdapter(0, 23));
		mWheelViewHH.setLabel("点");
		mWheelViewHH.setTextSize(iTextSize);
		mWheelViewHH.setItemHeight(20);
		mWheelViewHH.setCurrentItem(defaultHour);
		mWheelViewHH.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 分
		mWheelViewMM.setAdapter(new NumericWheelAdapter(0, 59));
		mWheelViewMM.setLabel("分");
		mWheelViewMM.setTextSize(iTextSize);
		mWheelViewMM.setItemHeight(20);
		mWheelViewMM.setCurrentItem(defaultMinute);
		mWheelViewMM.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		okBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
				int index2 = mWheelViewHH.getCurrentItem();
				int index3 = mWheelViewMM.getCurrentItem();
				String val = DateUtil.dateTimeFormat(index2+":"+index3) ;
				mText.setText(val);
			}

		});

		cancelBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
			}

		});

	}
	/**
	 * 描述：时分的时间选择器.（确定事件在外部处理）
	 *
	 * @param mContext
	 * @param iTextSize 字体大小SP
	 * @param iCenterSelectDrawablefinal 选中覆盖背景
	 * @param mWheelViewHH the m wheel view hh
	 * @param mWheelViewMM  选择分的轮子
	 * @param cancelBtn 取消按钮
	 * @param defaultHour the default hour
	 * @param defaultMinute the default minute
	 * @param initStart
	 */
	public static void initWheelTimePicker2(final Context mContext, int iTextSize, int iCenterSelectDrawablefinal, final WheelView mWheelViewHH, final WheelView mWheelViewMM,
                                            Button cancelBtn, int defaultHour, int defaultMinute, boolean initStart){
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		if(initStart){
			defaultHour = hour;
			defaultMinute = minute;
		}

		// 时
		mWheelViewHH.setAdapter(new NumericWheelAdapter(0, 23));
		mWheelViewHH.setLabel("点");
		mWheelViewHH.setTextSize(iTextSize);
		mWheelViewHH.setItemHeight(20);
		mWheelViewHH.setCurrentItem(defaultHour);
		mWheelViewHH.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 分
		mWheelViewMM.setAdapter(new NumericWheelAdapter(0, 59));
		mWheelViewMM.setLabel("分");
		mWheelViewMM.setTextSize(iTextSize);
		mWheelViewMM.setItemHeight(20);
		mWheelViewMM.setCurrentItem(defaultMinute);
		mWheelViewMM.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		cancelBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
			}

		});

	}


	/**
	 * 描述：默认的年月日时分的日期选择器.
	 *
	 * @param mContext
	 * @param mText the m text
	 * @param iTextSize 字体大小SP
	 * @param iCenterSelectDrawablefinal 选中覆盖背景
	 * @param mWheelViewY  选择年的轮子
	 * @param mWheelViewM  选择月的轮子
	 * @param mWheelViewD  选择日的轮子
	 * @param mWheelViewHH 选择小时的轮子
	 * @param mWheelViewMM  选择分的轮子
	 * @param okBtn 确定按钮
	 * @param cancelBtn 取消按钮
	 * @param defaultYear  默认显示的年
	 * @param defaultMonth the default month
	 * @param defaultDay the default day
	 * @param defaultHour the default hour
	 * @param defaultMinute the default minute
	 * @param startYear    开始的年
	 * @param endYearOffset 结束的年与开始的年的偏移
	 * @param initStart  轮子是否初始化默认当前日期
	 */
	public static void initWheelDateTimePicker(final Context mContext, final TextView mText, int iTextSize, int iCenterSelectDrawablefinal,
                                               final WheelView mWheelViewY, final WheelView mWheelViewM, final WheelView mWheelViewD,
                                               final WheelView mWheelViewHH, final WheelView mWheelViewMM,
                                               Button okBtn, Button cancelBtn,
                                               int defaultYear, int defaultMonth, int defaultDay, int defaultHour, int defaultMinute,
                                               final int startYear, int endYearOffset, boolean initStart){

		int endYear = startYear+endYearOffset;
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };
		//时间选择可以这样实现
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		if(initStart){
			defaultYear = year;
			defaultMonth = month;
			defaultDay = day;
			defaultHour = hour;
			defaultMinute = minute;
		}

//		mText.setText(StrUtil.dateTimeFormat(defaultYear+"-"+defaultMonth+"-"+defaultDay));
		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		//设置"年"的显示数据
		mWheelViewY.setAdapter(new NumericWheelAdapter(startYear, endYear));
		mWheelViewY.setLabel("年");  // 添加文字
		mWheelViewY.setTextSize(iTextSize);
		mWheelViewY.setItemHeight(20);
		mWheelViewY.setCurrentItem(defaultYear - startYear);// 初始化时显示的数据
		mWheelViewY.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 月
		mWheelViewM.setAdapter(new NumericWheelAdapter(1, 12));
		mWheelViewM.setLabel("月");
		mWheelViewM.setTextSize(iTextSize);
		mWheelViewM.setItemHeight(20);
		mWheelViewM.setCurrentItem(defaultMonth-1);
		mWheelViewM.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 日
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(defaultMonth))) {
			mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(defaultMonth))) {
			mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if (DateUtil.isLeapYear(year)){
				mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
			}else{
				mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
			}
		}
		mWheelViewD.setLabel("日");
		mWheelViewD.setTextSize(iTextSize);
		mWheelViewD.setItemHeight(20);
		mWheelViewD.setCurrentItem(defaultDay - 1);
		mWheelViewD.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));
		// 时
		mWheelViewHH.setAdapter(new NumericWheelAdapter(0, 23));
		mWheelViewHH.setLabel("时");
		mWheelViewHH.setTextSize(iTextSize);
		mWheelViewHH.setItemHeight(20);
		mWheelViewHH.setCurrentItem(defaultHour);
		mWheelViewHH.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 分
		mWheelViewMM.setAdapter(new NumericWheelAdapter(0, 59));
		mWheelViewMM.setLabel("分");
		mWheelViewMM.setTextSize(iTextSize);
		mWheelViewMM.setItemHeight(20);
		mWheelViewMM.setCurrentItem(defaultMinute);
		mWheelViewMM.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));
		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {

			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + startYear;
				int mDIndex = mWheelViewM.getCurrentItem();
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(mWheelViewM.getCurrentItem() + 1))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(mWheelViewM.getCurrentItem() + 1))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (DateUtil.isLeapYear(year_num))
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
					else
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
				}
				mWheelViewM.setCurrentItem(mDIndex);

			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {

			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					int year_num = mWheelViewY.getCurrentItem() + startYear;
					if (DateUtil.isLeapYear(year_num))
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
					else
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
				}
				mWheelViewD.setCurrentItem(0);
			}
		};
		mWheelViewY.addChangingListener(wheelListener_year);
		mWheelViewM.addChangingListener(wheelListener_month);

		okBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
				int indexYear = mWheelViewY.getCurrentItem();
				String year = mWheelViewY.getAdapter().getItem(indexYear);

				int indexMonth = mWheelViewM.getCurrentItem();
				String month = mWheelViewM.getAdapter().getItem(indexMonth);

				int indexDay = mWheelViewD.getCurrentItem();
				String day = mWheelViewD.getAdapter().getItem(indexDay);
				int index2 = mWheelViewHH.getCurrentItem();
				int index3 = mWheelViewMM.getCurrentItem();
				String val = DateUtil.dateTimeFormat(index2 + ":" + index3) ;
				mText.setText(DateUtil.dateTimeFormat(year+"-"+month+"-"+day)+" "+val);
			}

		});

		cancelBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
			}
		});

	}

	/**
	 * 描述：默认的年月日时分的日期选择器.(所选日期不能晚于当前日期)
	 *
	 * @param mContext
	 * @param mText the m text
	 * @param iTextSize 字体大小SP
	 * @param iCenterSelectDrawablefinal 选中覆盖背景
	 * @param mWheelViewY  选择年的轮子
	 * @param mWheelViewM  选择月的轮子
	 * @param mWheelViewD  选择日的轮子
	 * @param mWheelViewHH 选择小时的轮子
	 * @param mWheelViewMM  选择分的轮子
	 * @param okBtn 确定按钮
	 * @param cancelBtn 取消按钮
	 * @param defaultYear  默认显示的年
	 * @param defaultMonth the default month
	 * @param defaultDay the default day
	 * @param defaultHour the default hour
	 * @param defaultMinute the default minute
	 * @param startYear    开始的年
	 * @param endYearOffset 结束的年与开始的年的偏移
	 * @param initStart  轮子是否初始化默认当前日期
	 */
	public static void initWheelDateTimePickers(final Context mContext, final TextView mText, int iTextSize, int iCenterSelectDrawablefinal,
                                                final WheelView mWheelViewY, final WheelView mWheelViewM, final WheelView mWheelViewD,
                                                final WheelView mWheelViewHH, final WheelView mWheelViewMM,
                                                Button okBtn, Button cancelBtn,
                                                int defaultYear, int defaultMonth, int defaultDay, int defaultHour, int defaultMinute,
                                                final int startYear, int endYearOffset, boolean initStart){

		int endYear = startYear+endYearOffset;
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };
		//时间选择可以这样实现
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH)+1;
		int day = calendar.get(Calendar.DATE);
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);

		if(initStart){
			defaultYear = year;
			defaultMonth = month;
			defaultDay = day;
			defaultHour = hour;
			defaultMinute = minute;
		}

//		mText.setText(StrUtil.dateTimeFormat(defaultYear+"-"+defaultMonth+"-"+defaultDay));
		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		//设置"年"的显示数据
		mWheelViewY.setAdapter(new NumericWheelAdapter(startYear, endYear));
		mWheelViewY.setLabel("年");  // 添加文字
		mWheelViewY.setTextSize(iTextSize);
		mWheelViewY.setItemHeight(20);
		mWheelViewY.setCurrentItem(defaultYear - startYear);// 初始化时显示的数据
		mWheelViewY.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 月
		mWheelViewM.setAdapter(new NumericWheelAdapter(1, 12));
		mWheelViewM.setLabel("月");
		mWheelViewM.setTextSize(iTextSize);
		mWheelViewM.setItemHeight(20);
		mWheelViewM.setCurrentItem(defaultMonth-1);
		mWheelViewM.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 日
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(defaultMonth))) {
			mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(defaultMonth))) {
			mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if (DateUtil.isLeapYear(year)){
				mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
			}else{
				mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
			}
		}
		mWheelViewD.setLabel("日");
		mWheelViewD.setTextSize(iTextSize);
		mWheelViewD.setItemHeight(20);
		mWheelViewD.setCurrentItem(defaultDay - 1);
		mWheelViewD.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));
		// 时
		mWheelViewHH.setAdapter(new NumericWheelAdapter(0, 23));
		mWheelViewHH.setLabel("时");
		mWheelViewHH.setTextSize(iTextSize);
		mWheelViewHH.setItemHeight(20);
		mWheelViewHH.setCurrentItem(defaultHour);
		mWheelViewHH.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		// 分
		mWheelViewMM.setAdapter(new NumericWheelAdapter(0, 59));
		mWheelViewMM.setLabel("分");
		mWheelViewMM.setTextSize(iTextSize);
		mWheelViewMM.setItemHeight(20);
		mWheelViewMM.setCurrentItem(defaultMinute);
		mWheelViewMM.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));
		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {

			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + startYear;
				int mDIndex = mWheelViewM.getCurrentItem();
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(mWheelViewM.getCurrentItem() + 1))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(mWheelViewM.getCurrentItem() + 1))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if (DateUtil.isLeapYear(year_num))
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
					else
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
				}
				mWheelViewM.setCurrentItem(mDIndex);

			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {

			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					mWheelViewD.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					int year_num = mWheelViewY.getCurrentItem() + startYear;
					if (DateUtil.isLeapYear(year_num))
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 29));
					else
						mWheelViewD.setAdapter(new NumericWheelAdapter(1, 28));
				}
				mWheelViewD.setCurrentItem(0);
			}
		};
		mWheelViewY.addChangingListener(wheelListener_year);
		mWheelViewM.addChangingListener(wheelListener_month);

		okBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				int indexYear = mWheelViewY.getCurrentItem();
				String year = mWheelViewY.getAdapter().getItem(indexYear);

				int indexMonth = mWheelViewM.getCurrentItem();
				String month = mWheelViewM.getAdapter().getItem(indexMonth);

				int indexDay = mWheelViewD.getCurrentItem();
				String day = mWheelViewD.getAdapter().getItem(indexDay);
				int index2 = mWheelViewHH.getCurrentItem();
				int index3 = mWheelViewMM.getCurrentItem();
				String val = DateUtil.dateTimeFormat(index2 + ":" + index3) ;
				if(DateUtil.isAfter(DateUtil.dateTimeFormat(year+"-"+month+"-"+day)+" "+val,"yyyy-MM-dd HH:mm")){
					mText.setText(DateUtil.dateTimeFormat(year+"-"+month+"-"+day)+" "+val);
					DialogUtil.removeDialog(v);
				}else {
					Toast.makeText(mContext,"所选日期不能晚于当前日期", Toast.LENGTH_SHORT).show();
				}
			}

		});

		cancelBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
			}
		});

	}

	/**
	 * 描述： 字符串选择器
	 * @param mContext
	 * @param mArrayWheelAdapter
	 * @param mText 控件，用去确定后赋值
	 * @param iTextSize 字体大小SP
	 * @param iCenterSelectDrawablefinal 选中覆盖背景
	 * @param iCurrentIndex 默认选择项
	 * @param mWheelView 选择的轮子
	 * @param okBtn 确定按钮
	 * @param cancelBtn 取消按钮
	 */
	public static void initWheelArrayStr(final Context mContext, final ArrayWheelAdapter mArrayWheelAdapter, final TextView mText, int iTextSize, int iCenterSelectDrawablefinal,
                                         int iCurrentIndex, final WheelView mWheelView, Button okBtn, Button cancelBtn)
	{
		mWheelView.setAdapter(mArrayWheelAdapter);
		mWheelView.setTextSize(iTextSize);
		mWheelView.setItemHeight(20);
		mWheelView.setCurrentItem(iCurrentIndex);
		mWheelView.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));
		okBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
				String val =mWheelView.getAdapter().getItem(mWheelView.getCurrentItem());
				mText.setText(val);
			}
		});

		cancelBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
			}

		});
	}
	/**
	 * 描述： 字符串选择器（确定事件在外部处理）
	 * @param mContext
	 * @param mArrayWheelAdapter
	 * @param iTextSize 字体大小SP
	 * @param iCenterSelectDrawablefinal 选中覆盖背景
	 * @param iCurrentIndex 默认选择项
	 * @param mWheelView 选择的轮子
	 * @param cancelBtn 取消按钮
	 */
	public static void initWheelArrayStr(final Context mContext, final ArrayWheelAdapter mArrayWheelAdapter, int iTextSize, int iCenterSelectDrawablefinal,
                                         int iCurrentIndex, final WheelView mWheelView, Button cancelBtn)
	{
		mWheelView.setAdapter(mArrayWheelAdapter);
		mWheelView.setTextSize(iTextSize);
		mWheelView.setItemHeight(20);
		mWheelView.setCurrentItem(iCurrentIndex);
		mWheelView.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));
		cancelBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
			}

		});
	}
	/**
	 * 描述：区域选择器
	 * @param mContext
	 * @param arrProvice 省份
	 * @param arrCity 城市
	 * @param iTextSize 字体大小SP
	 * @param iCenterSelectDrawablefinal 选中覆盖背景
	 * @param iCurrentIndexQY1
	 * @param iCurrentIndexQY2
	 * @param mWheelViewQY1 选择分的轮子
	 * @param mWheelViewQY2 选择分的轮子
	 * @param cancelBtn
	 */
	public static void initWheelArea(final Context mContext, final String[] arrProvice, final String[][] arrCity, int iTextSize, int iCenterSelectDrawablefinal,
                                     int iCurrentIndexQY1, int iCurrentIndexQY2, final WheelView mWheelViewQY1, final WheelView mWheelViewQY2, Button cancelBtn)
	{
		ArrayWheelAdapter<String> mQY1Adapter= new ArrayWheelAdapter<>(arrProvice);
		mWheelViewQY1.setAdapter(mQY1Adapter);
		mWheelViewQY1.setTextSize(iTextSize);
		mWheelViewQY1.setItemHeight(20);
		mWheelViewQY1.setCurrentItem(iCurrentIndexQY1);
		mWheelViewQY1.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));

		mWheelViewQY2.setAdapter(new ArrayWheelAdapter<>(arrCity[iCurrentIndexQY1]));
		mWheelViewQY2.setTextSize(iTextSize);
		mWheelViewQY2.setItemHeight(20);
		mWheelViewQY2.setCurrentItem(iCurrentIndexQY2);
		mWheelViewQY2.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));
		OnWheelChangedListener wheelListener_QY1=new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				ArrayWheelAdapter<String> mQY2Adapter= new ArrayWheelAdapter<>(arrCity[mWheelViewQY1.getCurrentItem()]);
				mWheelViewQY2.setAdapter(mQY2Adapter);
				mWheelViewQY2.setCurrentItem(0);
			}
		};
		mWheelViewQY1.addChangingListener(wheelListener_QY1);
		cancelBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
			}
		});
	}
	/**
	 * 描述：简单数字选择器
	 * @param mContext
	 * @param mText 控件，用去确定后赋值
	 * @param iTextSize 字体大小SP
	 * @param iCenterSelectDrawablefinal 选中覆盖背景
	 * @param iMaxNum 最大数
	 * @param iMinNum 最小数
	 * @param iCurrentIndex  默认选择项
	 * @param sUnit  单位（例如：kg cm 等，如果空设置成null）
	 * @param mWheelView 选择数字的轮子
	 * @param okBtn 确定按钮
	 * @param cancelBtn 取消按钮
	 */
	public static void initWheelSimpleNum(final Context mContext, final TextView mText, int iTextSize, int iCenterSelectDrawablefinal,
                                          int iMaxNum, int iMinNum, int iCurrentIndex, final String sUnit, final WheelView mWheelView, Button okBtn, Button cancelBtn)
	{
		mWheelView.setAdapter(new NumericWheelAdapter(iMinNum, iMaxNum));
		mWheelView.setTextSize(iTextSize);
		if(!StringUtils.isEmpty(sUnit))
			mWheelView.setLabel(sUnit);
		mWheelView.setCurrentItem(iCurrentIndex);
		mWheelView.setItemHeight(20);
		mWheelView.setCenterSelectDrawable(mContext.getResources().getDrawable(iCenterSelectDrawablefinal));
		okBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
				String val = null;
				val =mWheelView.getAdapter().getItem(mWheelView.getCurrentItem());
				mText.setText(val);
			}
		});

		cancelBtn.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				DialogUtil.removeDialog(v);
			}

		});
	}
}
