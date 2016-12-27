package com.example.suren.nerdlauncher;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class NerdLauncherFragment extends Fragment {
    private final static String LOG_TAG = NerdLauncherFragment.class.getSimpleName();

    private RecyclerView mRecyclerView;
    private List<ResolveInfo> mActivities;

    private class ActivityHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private ResolveInfo mResolveInfo;
        private TextView mTextView;
        private ImageView mImageView;

        public ActivityHolder(View view) {
            super(view);
            mTextView = (TextView)view.findViewById(R.id.list_item_app_title);
            mTextView.setOnClickListener(this);
            mImageView = (ImageView)view.findViewById(R.id.list_item_app_icon);
        }

        public void bindActivity(ResolveInfo activity) {
            mResolveInfo = activity;
            PackageManager pm = getActivity().getPackageManager();
            mTextView.setText(mResolveInfo.loadLabel(pm));
            mImageView.setImageDrawable(mResolveInfo.loadIcon(pm));
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(Intent.ACTION_MAIN).
                    setClassName(mResolveInfo.activityInfo.packageName,
                            mResolveInfo.activityInfo.name).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
    }

    public NerdLauncherFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nerd_launcher, container, false);
        mRecyclerView = (RecyclerView)view.findViewById(R.id.fragment_nerd_launcher_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setupAdapter();
        return view;
    }

    private void setupAdapter() {
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        PackageManager pm = getActivity().getPackageManager();
        mActivities = pm.queryIntentActivities(startIntent, 0);
        Collections.sort(mActivities, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo r1, ResolveInfo r2) {
                PackageManager pm = getActivity().getPackageManager();
                String label1 = r1.loadLabel(pm).toString();
                String label2 = r2.loadLabel(pm).toString();
                return label1.compareTo(label2);
            }
        });
        Log.i(LOG_TAG, "Number of activities found: " + mActivities.size());
        mRecyclerView.setAdapter(new RecyclerView.Adapter() {
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.list_item_fragment, parent, false);
                return new ActivityHolder(view);
            }

            @Override
            public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
                ((ActivityHolder)holder).bindActivity(mActivities.get(position));
            }

            @Override
            public int getItemCount() {
                return mActivities.size();
            }
        });
    }
}