package com.upool.android.upool.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.upool.android.upool.Models.AutoCompletePlace;
import com.upool.android.upool.R;

import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Darren on 7/14/2017.
 */

public class AddressAutoCompleteAdapter extends RecyclerView.Adapter<AddressAutoCompleteAdapter.ViewHolder> implements Filterable {
    private static final String TAG = "AddressACAdapter";

    private GoogleApiClient googleApiClient;
    private Context context;
    private ArrayList<AutoCompletePlace> autoCompletePlacesList;
//    private AddressFilter addressFilter;
    private Filter addressFilter;

    public AddressAutoCompleteAdapter(@NonNull Context context) {
        this.context = context;
        this.autoCompletePlacesList = new ArrayList<>();
    }

    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        this.googleApiClient = googleApiClient;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_auto_complete_address, parent, false);
        return new ViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AutoCompletePlace autoCompletePlace = autoCompletePlacesList.get(position);

        if(autoCompletePlace != null) {
            if(holder.autoCompleteAddressTV != null) {
                holder.autoCompleteAddressTV.setText(autoCompletePlace.getDescription());
            }
        }
    }

    @Override
    public int getItemCount() {
        return autoCompletePlacesList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder  {
        @BindView(R.id.autoCompleteAddressImageView)
        ImageView autoCompleteAddressIV;
        @BindView(R.id.autoCompleteAddressTextView)
        TextView autoCompleteAddressTV;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    @Override
    public Filter getFilter() {
        if(addressFilter == null)
//            addressFilter = new AddressFilter(googleApiClient);
        addressFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                if(googleApiClient == null || !googleApiClient.isConnected()) {
                    Log.d(TAG, "googleApiClient not connected");
                    return null;
                }

                if(charSequence != null)
                    displayPredictiveResults(charSequence.toString());

                return null;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                notifyDataSetChanged();
            }
        };
        return addressFilter;
    }

    private void displayPredictiveResults(final String query) {
        AutocompleteFilter typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_ADDRESS)
                .setCountry(Locale.getDefault().getCountry())
                .build();
        Places.GeoDataApi.getAutocompletePredictions(googleApiClient, query, null, typeFilter)
                .setResultCallback(
                        new ResultCallback<AutocompletePredictionBuffer>() {
                            @Override
                            public void onResult(@NonNull AutocompletePredictionBuffer autocompletePredictions) {
                                if(autocompletePredictions == null)
                                    return;
                                if(autocompletePredictions.getStatus().isSuccess()) {
                                    autoCompletePlacesList.clear();
                                    for(AutocompletePrediction prediction : autocompletePredictions) {
                                        if(!query.equals(prediction.getFullText(null).toString()))
                                            //Add as a new item to avoid IllegalArgumentsException when buffer is released
                                            autoCompletePlacesList.add(new AutoCompletePlace(prediction.getPlaceId(), prediction.getFullText(null).toString()));
                                    }
                                }

                                //Prevent memory leak by releasing buffer
                                autocompletePredictions.release();
                            }
                        }, 60, TimeUnit.SECONDS
                );
    }
}
