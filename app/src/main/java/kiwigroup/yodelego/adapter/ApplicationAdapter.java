package kiwigroup.yodelego.adapter;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import kiwigroup.yodelego.OnUserFragmentsListener;
import kiwigroup.yodelego.R;
import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer.OfferStatus;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ApplicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_EMPTY = 2;
    private final OnUserFragmentsListener mListener;
    private List<Application> applications;
    private Application.ApplicationStatus statusFilter;

    public ApplicationAdapter(OnUserFragmentsListener listener, Application.ApplicationStatus filter) {
        applications = new ArrayList<>();
        mListener = listener;
        statusFilter = filter;
    }

    public void update(List<Application> applications) {
        this.applications.clear();
        for(Application app : applications){
            if(app.getApplicationStatus() == statusFilter)
                this.applications.add(app);
        }
        notifyDataSetChanged();
    }

    public void showLoading() {
        applications = new ArrayList<>();
        applications.add(null);
        notifyDataSetChanged();
    }

    public void hideLoading() {
        applications.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return applications.isEmpty() ?
                VIEW_TYPE_EMPTY :
                applications.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ITEM) {
            View v = inflater.inflate(R.layout.fragment_postulation_item, parent, false);
            return new ApplicationViewHolder(v);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = inflater.inflate(R.layout.content_item_loading, parent, false);
            return new LoadingViewHolder(v);
        } else if (viewType == VIEW_TYPE_EMPTY) {
            View v = inflater.inflate(R.layout.fragment_postulation_empty, parent, false);
            return new EmptyViewHolder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ApplicationViewHolder) {
            ApplicationViewHolder offerViewHolder = (ApplicationViewHolder) holder;
            final Application offer = applications.get(position);

            switch(offer.getApplicationStatus()){
                case CANCELED:
                    offerViewHolder.status.setText("cerrado");
                    offerViewHolder.status.getBackground().setColorFilter(
                            ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorRed),
                            PorterDuff.Mode.SRC);
                    break;
                case REVISION:
                    offerViewHolder.status.setText("revisión");
                    offerViewHolder.status.getBackground().setColorFilter(
                            ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorAcademicShape),
                            PorterDuff.Mode.SRC);
                    break;
                case ACCEPTED:
                    offerViewHolder.status.setText("adjudicada");
                    offerViewHolder.status.getBackground().setColorFilter(
                            ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorAdjudicated),
                            PorterDuff.Mode.SRC);
                    break;
            }
            offerViewHolder.resume.setText(Html.fromHtml("<b>Lily Anguita</b>  publicó un nuevo trabajo"));
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
            otherSymbols.setDecimalSeparator(',');
            otherSymbols.setGroupingSeparator('.');
            offerViewHolder.amount.setText(String.format("$%s", new DecimalFormat("#,###", otherSymbols).format(offer.getDailyWage())));
        } else if(holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        } else if(holder instanceof EmptyViewHolder) {
            EmptyViewHolder loadingViewHolder = (EmptyViewHolder) holder;
        }
    }

    @Override
    public int getItemCount() {
        return applications.isEmpty() ? 1 : applications.size();
    }

    class ApplicationViewHolder extends RecyclerView.ViewHolder {
        TextView resume;
        Button status;
        TextView amount;
        public View layout;

        ApplicationViewHolder(View view) {
            super(view);
            layout = view;
            resume = view.findViewById(R.id.publication_resume);
            status = view.findViewById(R.id.status_button);
            amount = view.findViewById(R.id.amount);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }

    class EmptyViewHolder extends RecyclerView.ViewHolder {

        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
