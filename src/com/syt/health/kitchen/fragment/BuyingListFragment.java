package com.syt.health.kitchen.fragment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.syt.health.kitchen.BaseActivity;
import com.syt.health.kitchen.BuyingActivity;
import com.syt.health.kitchen.R;
import com.syt.health.kitchen.json.CourseFood;
import com.syt.health.kitchen.json.Food;
import com.syt.health.kitchen.service.MessageModel;
import com.syt.health.kitchen.service.ServiceImpl;
import com.syt.health.kitchen.service.TaskCallBack;
import com.syt.health.kitchen.utils.DownloadUtil;
import com.syt.health.kitchen.utils.Utils;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;

public class BuyingListFragment extends Fragment {
	public final static String cache_path = "/mnt/sdcard/kitchen/";
	public final static String TAG = "BUYINGCHOICEFRAGMENT";
	public final static String FLAG = "FLAG";
	private Button edit_btn;
	private List<CourseFood> courseFoods = new ArrayList<CourseFood>();
	private ListView courseFood_lv;
	private String courseFood_desc;
	private ListAdapter adapter;
	private ServiceImpl serviceImpl;
	private boolean isEditable;

	private boolean isSave;

	// private DownloadUtil util = new DownloadUtil();
	private ArrayList<Food> foodList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		serviceImpl = ((BaseActivity) getActivity()).getService();
		if (serviceImpl.getCurrentUser() != null) {
			isEditable = true;
			courseFoods = serviceImpl.getCurrentUser().getObjFoodList();
		} else {
			isEditable = false;
			TelephonyManager manager = (TelephonyManager) getActivity()
					.getSystemService(Context.TELEPHONY_SERVICE);
			String sid = manager.getDeviceId();
			courseFoods = serviceImpl.getFoodList(sid);
		}
		courseFood_desc = serviceImpl.getCurrentUser().getFoodListDesc();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		((BuyingActivity) getActivity()).setExit(true);

		View view = inflater.inflate(R.layout.fragment_buyinglist, container,
				false);

		if (courseFoods != null && courseFoods.size() != 0) {
			init(view);
			if (isEditable) {
				edit_btn.setVisibility(View.VISIBLE);
			} else {
				edit_btn.setVisibility(View.GONE);
			}
			final List<String> fList = new ArrayList<String>();
			foodList = new ArrayList<Food>();
			for (CourseFood cf : courseFoods) {
				if (cf.getFood().hasBible()) {
					fList.add(cf.getFood().getId());
					foodList.add(cf.getFood());
				}

			}
			if (isSave) {
				final ProgressDialog pd = ProgressDialog.show(getActivity(), "",
						getResources().getString(R.string.cache_food_list));
				
				pd.show();

				// util.runSaveUrl(fList, cache_path, null);
				ServiceImpl service = ((BaseActivity) getActivity()).getService();
				service.cacheFoods(new TaskCallBack<List<String>, MessageModel<String>>() {

					@Override
					public void postExecute(MessageModel<String> result) {
						pd.dismiss();

					}

					@Override
					public List<String> getParameters() {
						return fList;
					}
				});

			}
		} else {
			// new
			// AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.dialog_title)).
			// setMessage("您还未选择您需要购买的食材，是否跳到选择界面？").
			// setPositiveButton(getResources().getString(R.string.yes), new
			// DialogInterface.OnClickListener() {
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// dialog.dismiss();
			((BaseActivity) getActivity()).addFragment(
					BuyEditorFragment.newInstance(), BuyEditorFragment.TAG,
					android.R.id.content);
			// }
			// }).setNegativeButton(android.R.string.no, new
			// DialogInterface.OnClickListener() {
			//
			// @Override
			// public void onClick(DialogInterface dialog, int which) {
			// dialog.dismiss();
			//
			// }
			// }).show();
		}

//		final List<String> fList = new ArrayList<String>();
//		foodList = new ArrayList<Food>();
//		for (CourseFood cf : courseFoods) {
//			if (cf.getFood().hasBible()) {
//				fList.add(cf.getFood().getId());
//				foodList.add(cf.getFood());
//			}
//
//		}
		

		return view;
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void init(View view) {
		edit_btn = (Button) view
				.findViewById(R.id.fragment_buyinglist_edit_btn);
		edit_btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				((BaseActivity) getActivity()).addFragment(
						BuyEditorFragment.newInstance(), BuyEditorFragment.TAG,
						android.R.id.content);

			}
		});

		courseFood_lv = (ListView) view
				.findViewById(R.id.fragment_buyinglist_coursefood_lv);

		adapter = new ListAdapter();
		courseFood_lv.setAdapter(adapter);

		TextView courseFoodDescTxt = (TextView) view
				.findViewById(R.id.fragment_buyinglist_desc);
		courseFoodDescTxt.setText(courseFood_desc);
		
		((BuyingActivity)getActivity()).setHealthList(serviceImpl.getMenuHealthByDate(courseFood_desc));
	}

	public void setSave(boolean isSave) {
		this.isSave = isSave;
	}

	public static BuyingListFragment newInstance(boolean isSave) {
		BuyingListFragment fragment = new BuyingListFragment();
		fragment.setSave(isSave);
		return fragment;
	}

	/**
	 * 重写listview适配器
	 * 
	 * @author ChengFaner
	 * 
	 */
	class ListAdapter extends BaseAdapter {
		@Override
		public int getCount() {
			return courseFoods.size();
		}

		@Override
		public CourseFood getItem(int arg0) {
			return courseFoods.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final Holder h;
			if (convertView == null) {
				convertView = LayoutInflater.from(getActivity()).inflate(
						R.layout.fragment_buying_list_item, null);
				h = new Holder(convertView);
				convertView.setTag(h);
			} else {
				h = (Holder) convertView.getTag();
			}
			final CourseFood courseFood = courseFoods.get(position);
			((BaseActivity) getActivity()).getmImageFetcher().loadImage(
					courseFood.getFood().getListPicUrl(), h.foodImg);
			h.cb.setText(courseFood.getFood().getName());
			h.cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

				@Override
				public void onCheckedChanged(CompoundButton buttonView,
						boolean isChecked) {
					courseFood.setFlag(isChecked);
					serviceImpl.saveFoodList(courseFoods, null);
					if (isChecked) {
						// h.cb.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
						// h.tv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
						h.cb.setTextColor(getResources().getColor(
								android.R.color.darker_gray));
						h.tv.setTextColor(getResources().getColor(
								android.R.color.darker_gray));

						h.ll.setVisibility(View.VISIBLE);
					} else {
						// h.cb.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
						// h.tv.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
						h.cb.setTextColor(getResources().getColor(
								android.R.color.black));
						h.tv.setTextColor(getResources().getColor(
								android.R.color.black));

						h.ll.setVisibility(View.GONE);
					}
				}
			});

			h.tv.setText(courseFood.getQuantity() + courseFood.getFoodunit());
			// h.cb.setChecked(courseFood.isFlag());

			if (courseFood.isFlag()) {
				// h.cb.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
				// h.tv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
				h.cb.setTextColor(getResources().getColor(
						android.R.color.darker_gray));
				h.tv.setTextColor(getResources().getColor(
						android.R.color.darker_gray));

				h.ll.setVisibility(View.VISIBLE);
			} else {
				// h.cb.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
				// h.tv.setPaintFlags(Paint.ANTI_ALIAS_FLAG);
				h.cb.setTextColor(getResources()
						.getColor(android.R.color.black));
				h.tv.setTextColor(getResources()
						.getColor(android.R.color.black));

				h.ll.setVisibility(View.GONE);
			}

			if (courseFood.getFood().hasBible()) {
				h.image.setVisibility(View.VISIBLE);
				h.image.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						((BaseActivity) getActivity()).addFragment(
								FoodInfoFragment.newInstance(courseFood.getFood()),
								FoodInfoFragment.TAG,
								android.R.id.content);
						// final String filename = cache_path +
						// bible.substring(bible.lastIndexOf("/")+1);
						// File dir = new File(filename);
						// if(!dir.exists()){
						//
						// final ProgressDialog pd =
						// ProgressDialog.show(getActivity(), "",
						// getResources().getString(R.string.cache_food_list));
						// 
						// pd.show();
						//
						// Runnable run = new Runnable() {
						//
						// @Override
						// public void run() {
						// // jump to bible
						// String nfilename = util.saveHtml(bible, filename);
						// if(nfilename == null){
						// ((BaseActivity)getActivity()).addFragment(FoodBibleFragment.newInstance(bible),
						// TAG, android.R.id.content);
						// }else{
						// ((BaseActivity)getActivity()).addFragment(FoodBibleFragment.newInstance("file://"
						// + nfilename), TAG, android.R.id.content);
						// }
						// pd.dismiss();
						// }
						// };
						//
						// new Thread(run).start();
						// }else{
						// ((BaseActivity)getActivity()).addFragment(FoodBibleFragment.newInstance("file://"
						// + filename), TAG, android.R.id.content);
						// }

					}
				});
			} else {
				h.image.setVisibility(View.INVISIBLE);
			}
			return convertView;
		}
	}

	class Holder {
		ImageView foodImg;
		ImageView image;
		CheckBox cb;
		TextView tv;
		LinearLayout ll;

		Holder(View view) {
			cb = (CheckBox) view
					.findViewById(R.id.fragment_buying_list_item_coursename_tv);
			tv = (TextView) view
					.findViewById(R.id.fragment_buying_list_item_courseweight_tv);
			image = (ImageView) view
					.findViewById(R.id.fragment_buying_list_item_payments_view);
			foodImg = (ImageView) view
					.findViewById(R.id.fragment_buying_list_item_foodimg);
			ll = (LinearLayout) view
					.findViewById(R.id.fragment_buying_list_item_delete);
		}
	}
}
