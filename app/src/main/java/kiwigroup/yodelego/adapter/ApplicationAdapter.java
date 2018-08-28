package kiwigroup.yodelego.adapter;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import kiwigroup.yodelego.OnUserFragmentsListener;
import kiwigroup.yodelego.R;
import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static kiwigroup.yodelego.model.Application.ApplicationStatus.ACCEPTED;
import static kiwigroup.yodelego.model.Application.ApplicationStatus.REVISION;

public class ApplicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_EMPTY = 2;
    private final OnUserFragmentsListener mListener;
    private List<Offer> applications;
    private Application.ApplicationStatus statusFilter;
    private boolean completeFilter;

    public ApplicationAdapter(OnUserFragmentsListener listener, Application.ApplicationStatus filter) {
        applications = new ArrayList<>();
        mListener = listener;
        statusFilter = filter;
    }

    public ApplicationAdapter(OnUserFragmentsListener listener, boolean completeFilter) {
        applications = new ArrayList<>();
        mListener = listener;
        this.completeFilter = completeFilter;
    }

    public void update(List<Offer> applications) {
        this.applications.clear();
        for(Offer offer : applications){
            if(completeFilter){
                Date currentTime = Calendar.getInstance().getTime();
                if((offer.getEndDate() == null || currentTime.after(offer.getEndDate())) &&
                    //offer.getStatus() != Offer.OfferStatus.CANCELED &&
                    //offer.getStatus() != Offer.OfferStatus.DEACTIVATED &&
                    //offer.getStatus() != Offer.OfferStatus.PAUSED &&
                    offer.getApplication().getApplicationStatus() == Application.ApplicationStatus.ACCEPTED) {
                    this.applications.add(offer);
                }
            } else if(offer.getApplication().getApplicationStatus() == statusFilter)
                this.applications.add(offer);
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
            View v = inflater.inflate(R.layout.fragment_application_item, parent, false);
            return new ApplicationViewHolder(v);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = inflater.inflate(R.layout.content_item_loading, parent, false);
            return new LoadingViewHolder(v);
        } else if (viewType == VIEW_TYPE_EMPTY) {
            View v = inflater.inflate(R.layout.fragment_application_empty, parent, false);
            return new EmptyViewHolder(v);
        }
        return null;
    }

    public void clear() {
        applications.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ApplicationViewHolder) {
            ApplicationViewHolder offerViewHolder = (ApplicationViewHolder) holder;
            final Offer offer = applications.get(position);

            switch(offer.getApplication().getApplicationStatus()){
                case CANCELED_BY_APPLICANT:
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
            offerViewHolder.resume.setText(offer.getTitle());
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
            otherSymbols.setDecimalSeparator(',');
            otherSymbols.setGroupingSeparator('.');
            offerViewHolder.amount.setText(
                    String.format("$%s", new DecimalFormat("#,###", otherSymbols).format(
                            offer.getTotalWage())));

            ((ApplicationViewHolder) holder).layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onApplicationSelected(offer);
                }
            });
        } else if(holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        } else if(holder instanceof EmptyViewHolder) {
            EmptyViewHolder emptyViewHolder = (EmptyViewHolder) holder;
            if(completeFilter)
                emptyViewHolder.resume.setText("no tienes postulaciones completadas");
            else if(statusFilter == ACCEPTED)
                emptyViewHolder.resume.setText("no tienes postulaciones adjudicadas");
            else if(statusFilter == REVISION)
                emptyViewHolder.resume.setText("no tienes postulaciones en revisión");
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
        TextView resume;

        EmptyViewHolder(View itemView) {
            super(itemView);
            resume = itemView.findViewById(R.id.publication_resume);
        }
    }
}
