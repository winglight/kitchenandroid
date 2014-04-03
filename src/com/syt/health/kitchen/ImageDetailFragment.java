/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.syt.health.kitchen;

import com.syt.health.kitchen.utils.Utils;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * This fragment will populate the children of the ViewPager from {@link ImageDetailActivity}.
 */
public class ImageDetailFragment extends Fragment {
    private static final String IMAGE_DATA_EXTRA = "extra_image_data";
    private static final String INDEX = "index";
	private static final String TAG = "ImageDetailFragment";
    private int mImageRes;
    private ImageView mImageView;
    private int index;

    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageUrl The image url to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static ImageDetailFragment newInstance(int imageUrl,int index) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA, imageUrl);
        args.putInt(INDEX, index);
        f.setArguments(args);

        return f;
    }

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageDetailFragment() {}

    /**
     * Populate image using a url from extras, use the convenience factory method
     * {@link ImageDetailFragment#newInstance(String)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageRes = getArguments() != null ? getArguments().getInt(IMAGE_DATA_EXTRA) : null;
        index = getArguments() !=null?getArguments().getInt(INDEX):0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (ImageView) v.findViewById(R.id.imageView);
        LinearLayout layout = (LinearLayout)v.findViewById(R.id.point_layout);
        for (int i = 0; i < 8; i++) {
			ImageView iv = new ImageView(getActivity());
			iv.setPadding(10, 0, 0, 0);			
			if(i==index){
				iv.setImageResource(R.drawable.page_indicator_focused);
			}else{
				iv.setImageResource(R.drawable.page_indicator_unfocused);
			}
			layout.addView(iv);
		}
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        try{
        mImageView.setImageBitmap(Utils.getBitmap(getActivity(), mImageRes).get());
        }catch(RuntimeException re){
    		Log.e(TAG, "RuntimeException");
    	}catch(OutOfMemoryError oe){
    		Log.e(TAG, "OutOfMemoryError");
    	}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mImageView != null) {
            // Cancel any pending image work
            mImageView.setImageDrawable(null);
        }
    }
}
