package com.android.assignmnet.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.assignmnet.R;
import com.android.assignmnet.database.RepositoryDAO;
import com.android.assignmnet.model.RepoDbModel;
import com.android.assignmnet.util.Constants;
import com.android.assignmnet.util.DividerItemDecoration;
import com.android.assignmnet.util.Util;
import com.android.assignmnet.volley.VolleyRequestQueue;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private RecyclerView repositoryRecyclerView;
    private final String TAG = MainActivity.class.getSimpleName();
    private ArrayList<String> repoList = new ArrayList<>();
    private RepositoryListItemRecyclerViewAdapter mAdapter;
    private LinearLayout mProgressLayout;
    private int pageIndex = 1;
    private long repoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        repositoryRecyclerView = (RecyclerView) findViewById(R.id.repo_recycler_view);
        mProgressLayout = (LinearLayout) findViewById(R.id.progressLayout);

        mAdapter = new RepositoryListItemRecyclerViewAdapter(repoList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(MainActivity.this);
        repositoryRecyclerView.setLayoutManager(mLayoutManager);
        repositoryRecyclerView.addItemDecoration(new DividerItemDecoration(MainActivity.this, LinearLayoutManager.VERTICAL));
        repositoryRecyclerView.setAdapter(mAdapter);
        if (Util.isNetworkConnected(this)) {
            callApi(pageIndex + "", Constants.PER_PAGE_LIMIT);
        } else {
            checkIfDataExistsInDb(true);

        }
    }


    public void callApi(String page, String limit) {
        try {
            mProgressLayout.setVisibility(View.VISIBLE);
            String url = Constants.API_URL + "page=" + page + "&per_page=" + limit;

            Log.d(TAG, "URL is:" + url);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            mProgressLayout.setVisibility(View.GONE);
                            try {

                                JSONArray itemArray = new JSONArray(response);
                                Log.d(TAG, "Item Length  " + itemArray);
                                if (itemArray != null && itemArray.length() > 0) {
                                    for (int i = 0; i < itemArray.length(); i++) {
                                        JSONObject jsonObject = itemArray.getJSONObject(i);
                                        String name = jsonObject.getString("full_name");
                                        repoList.add(name);
                                    }
                                    RepositoryDAO objRepositoryDAO = new RepositoryDAO(MainActivity.this);
                                    Gson gson = new Gson();
                                    String data = gson.toJson(repoList);
                                    if (pageIndex == 1) {
                                        objRepositoryDAO.deleteRepoData();
                                        repoId = objRepositoryDAO.saveRepoData(data);
                                    } else {
                                        RepoDbModel objRepoDbModel = new RepoDbModel(repoId, data);
                                        objRepositoryDAO.updateRepoData(objRepoDbModel);
                                    }
                                    mAdapter.notifyDataSetChanged();
                                    pageIndex++;

                                } else {
                                    showToast(getString(R.string.no_item_found));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                showToast(getString(R.string.generic_error));
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.d(TAG, "" + error.toString());
                            mProgressLayout.setVisibility(View.GONE);
                            checkIfDataExistsInDb(true);
                        }
                    });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            VolleyRequestQueue.getInstance(this).addToRequestQueue(stringRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public class RepositoryListItemRecyclerViewAdapter extends RecyclerView.Adapter<RepositoryListItemRecyclerViewAdapter.ViewHolder> {
        private ArrayList<String> mValues;

        public RepositoryListItemRecyclerViewAdapter(ArrayList<String> items) {
            mValues = items;

        }

        @Override
        public RepositoryListItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.repo_item, parent, false);
            return new RepositoryListItemRecyclerViewAdapter.ViewHolder(view);
        }

        @Override
        public int getItemViewType(int position) {

            return position;
        }

        @Override
        public void onBindViewHolder(final RepositoryListItemRecyclerViewAdapter.ViewHolder holder, final int position) {
            holder.mItem = mValues.get(position);
            holder.tvCode.setText(holder.mItem + "");
            callApiIfRequire(position);


        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public TextView tvCode;
            public String mItem;


            public ViewHolder(View view) {
                super(view);
                mView = view;
                tvCode = (TextView) view.findViewById(R.id.tv_repo_name);

            }

            @Override
            public String toString() {
                return super.toString();
            }
        }

        private void callApiIfRequire(int position) {
            if (position == repoList.size() - Constants.API_SYC_INDEX) {
                Log.d(TAG, "Position is :" + position);

                if (Util.isNetworkConnected(MainActivity.this)) {
                    Log.d(TAG, "Page number is :" + pageIndex);
                    callApi(pageIndex + "", Constants.PER_PAGE_LIMIT);
                } else {
                    checkIfDataExistsInDb(false);
                }


            }
        }

    }

    private void checkIfDataExistsInDb(boolean load) {
        RepoDbModel objRepoDbModel = new RepositoryDAO(MainActivity.this).getRepoData();
        if (load == true && objRepoDbModel != null && !TextUtils.isEmpty(objRepoDbModel.getData())) {

            String data = objRepoDbModel.getData();

            repoList = new Gson().fromJson(data, new TypeToken<ArrayList<String>>() {
            }.getType());
            mAdapter = new RepositoryListItemRecyclerViewAdapter(repoList);
            Log.d(TAG, repoList.size() + "");
            repositoryRecyclerView.setAdapter(mAdapter);
        } else {
            showToast(getString(R.string.network_error));
        }
    }

}
