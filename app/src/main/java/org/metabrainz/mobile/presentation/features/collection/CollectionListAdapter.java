package org.metabrainz.mobile.presentation.features.collection;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.metabrainz.mobile.R;
import org.metabrainz.mobile.data.sources.CollectionUtils;
import org.metabrainz.mobile.data.sources.Constants;
import org.metabrainz.mobile.data.sources.api.entities.mbentity.Collection;

import java.util.List;

class CollectionListAdapter extends RecyclerView.Adapter<CollectionListAdapter.CollectionViewHolder> {

    private final List<Collection> collections;

    public CollectionListAdapter(List<Collection> collections) {
        this.collections = collections;
    }

    @NonNull
    @Override
    public CollectionListAdapter.CollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = (LayoutInflater) parent.getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return new CollectionViewHolder(inflater.inflate(R.layout.item_collection, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull CollectionListAdapter.CollectionViewHolder holder, int position) {
        Collection collection = collections.get(position);
        holder.bind(collection);
    }

    @Override
    public int getItemCount() {
        return collections.size();
    }

    static class CollectionViewHolder extends RecyclerView.ViewHolder {
        private final TextView collectionNameView;
        private final TextView collectionTypeView;
        private final TextView collectionCountView;
        private final TextView collectionEntityView;

        private final View view;

        CollectionViewHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            collectionCountView = itemView.findViewById(R.id.collection_count);
            collectionEntityView = itemView.findViewById(R.id.collection_entity);
            collectionNameView = itemView.findViewById(R.id.collection_name);
            collectionTypeView = itemView.findViewById(R.id.collection_type);
        }

        void bind(Collection collection) {
            collectionNameView.setText(collection.getName());
            collectionTypeView.setText(collection.getType());
            collectionEntityView.setText(collection.getEntityType());
            collectionCountView.setText(String.valueOf(collection.getCount()));

            view.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), CollectionDetailsActivity.class);
                intent.putExtra(Constants.MBID, collection.getMbid());
                intent.putExtra(Constants.TYPE, CollectionUtils.getCollectionEntityType(collection));
                v.getContext().startActivity(intent);
            });

        }
    }
}
