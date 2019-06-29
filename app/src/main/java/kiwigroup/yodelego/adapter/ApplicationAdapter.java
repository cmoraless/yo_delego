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

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import kiwigroup.yodelego.OnUserFragmentsListener;
import kiwigroup.yodelego.R;
import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ApplicationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_EMPTY = 2;
    private final OnUserFragmentsListener mListener;
    private List<Offer> applications;
    private boolean adjudicatedFilter;
    private boolean completeFilter;
    private boolean reviewingFilter;

    public ApplicationAdapter(OnUserFragmentsListener listener,
                              boolean adjudicatedFilter,
                              boolean completeFilter,
                              boolean reviewingFilter) {
        applications = new ArrayList<>();
        mListener = listener;
        this.adjudicatedFilter = adjudicatedFilter;
        this.completeFilter = completeFilter;
        this.reviewingFilter = reviewingFilter;
    }

    public void update(List<Offer> applications) {

        Collections.sort(applications, new Comparator<Offer>() {
            public int compare(Offer o1, Offer o2) {
                return o1.getCreationDate().compareTo(o2.getCreationDate());
            }
        });

        this.applications.clear();

        for(Offer offer : applications){

            if(offer.getStatus() == Offer.OfferStatus.CANCELED || offer.getStatus() == Offer.OfferStatus.PAUSED )
                continue;

            if(completeFilter){
                if(offer.hasFinished() &&
                    offer.getApplication().getApplicationStatus() == Application.ApplicationStatus.ACCEPTED &&
                    offer.isPaid() /*&&
                    !offer.getApplication().isClosed()*/) {
                    this.applications.add(offer);
                }
            } else if(adjudicatedFilter) {
                if(offer.getApplication().getApplicationStatus() == Application.ApplicationStatus.ACCEPTED
                        && offer.isPaid()
                        && !offer.hasFinished()
                        && !offer.getApplication().isClosed()) {
                    this.applications.add(offer);
                }
            } else if(reviewingFilter) {
                if(!offer.hasStarted()
                    && (!offer.isPaid() && offer.getApplication().getApplicationStatus() == Application.ApplicationStatus.ACCEPTED ||
                        offer.getApplication().getApplicationStatus() == Application.ApplicationStatus.REVISION)) {
                    this.applications.add(offer);
                }
            }
        }
        notifyDataSetChanged();
    }

    public void showLoading() {
        //applications = new ArrayList<>();
        applications.clear();
        applications.add(null);
        notifyDataSetChanged();
    }

    public void hideLoading() {
        if(applications.size() == 1 && applications.get(0) == null) {
            applications.clear();
            notifyDataSetChanged();
        }
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
        if(applications.size() != 1 || (applications.get(0) != null))
            applications.clear();
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ApplicationViewHolder) {
            ApplicationViewHolder offerViewHolder = (ApplicationViewHolder) holder;
            final Offer offer = applications.get(position);

            if(offer.getPublisher().getProfilePictureUrl() != null && !offer.getPublisher().getProfilePictureUrl().isEmpty()){
                Picasso.get().load(offer.getPublisher().getProfilePictureUrl()).into(offerViewHolder.profileImage);
            }

            if(adjudicatedFilter){
                offerViewHolder.status.setText("adjudicado");
                offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                        R.color.colorAdjudicated),
                        PorterDuff.Mode.SRC);
            } else if(reviewingFilter){
                offerViewHolder.status.setText("en revisión");
                offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                        R.color.colorAcademicShape),
                        PorterDuff.Mode.SRC);
            } else if(completeFilter){
                offerViewHolder.status.setText("completado");
                offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                        R.color.colorPrimaryDark),
                        PorterDuff.Mode.SRC);
            }

            offerViewHolder.resume.setText(offer.getTitle());
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(new Locale("es", "ES"));
            otherSymbols.setDecimalSeparator(',');
            otherSymbols.setGroupingSeparator('.');
            offerViewHolder.amount.setText(
                    String.format("$%s", new DecimalFormat("#,###", otherSymbols).format(
                            offer.getWage())));

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
            else if(adjudicatedFilter)
                emptyViewHolder.resume.setText("no tienes postulaciones adjudicadas");
            else if(reviewingFilter)
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
        CircleImageView profileImage;
        public View layout;

        ApplicationViewHolder(View view) {
            super(view);
            layout = view;
            profileImage = view.findViewById(R.id.profile_image);
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
