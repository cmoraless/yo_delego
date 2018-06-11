package kiwigroup.yodelego.adapter;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import kiwigroup.yodelego.R;
import kiwigroup.yodelego.model.NotificationResume;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.model.WallItem;

import static kiwigroup.yodelego.model.Application.ApplicationStatus.ACCEPTED;
import static kiwigroup.yodelego.model.Application.ApplicationStatus.CANCELED;
import static kiwigroup.yodelego.model.Application.ApplicationStatus.REJECTED;
import static kiwigroup.yodelego.model.Application.ApplicationStatus.REVISION;

/**
 * Created by cristian on 4/20/17.
 */

public class WallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private final int VIEW_TYPE_NOTIFICATION = 2;
    private AdapterListener listener;
    private List<WallItem> offers;

    public WallAdapter(AdapterListener listener) {
        this.listener = listener;
        offers = new ArrayList<>();
    }

    public void append(WallItem item) {
        int index = offers.size();
        offers.add(item);
        notifyItemInserted(index);
    }

    public void showLoading() {
        int index = offers.size();
        offers.add(null);
        notifyItemInserted(index);
    }

    public void hideLoading() {
        if(!offers.isEmpty()){
            int index = offers.size() - 1;
            offers.remove(index);
            notifyItemRemoved(index);
        }
    }

    public void addNotificationResume(NotificationResume notificationResume){
        if(offers.size() == 0){
            offers.add(notificationResume);
        } else {
            if(offers.get(0) instanceof NotificationResume){
                offers.set(0, notificationResume);
            } else {
                offers.add(0, notificationResume);
            }
        }
        notifyItemInserted(0);
    }

    public void clear() {
        offers.clear();
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ITEM) {
            View v = inflater.inflate(R.layout.content_wall_item, parent, false);
            return new OfferViewHolder(v);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = inflater.inflate(R.layout.content_item_loading, parent, false);
            return new LoadingViewHolder(v);
        } else if (viewType == VIEW_TYPE_NOTIFICATION) {
            View v = inflater.inflate(R.layout.content_wall_notification, parent, false);
            return new NotificationViewHolder(v);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return offers.get(position) == null ? VIEW_TYPE_LOADING :
                offers.get(position) instanceof Offer ? VIEW_TYPE_ITEM : VIEW_TYPE_NOTIFICATION;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OfferViewHolder) {
            OfferViewHolder offerViewHolder = (OfferViewHolder)holder;
            final Offer offer = (Offer) offers.get(position);
            Log.d("WallAdpater**** " , " ***** offer: " + offer.isApplied());
            offerViewHolder.resume.setText(Html.fromHtml("<b>" + offer.getPublisher() + "</b>  publicó un nuevo trabajo"));
            offerViewHolder.date.setText(DateUtils.getRelativeTimeSpanString(offer.getCreationDate().getTime(), new Date().getTime(),0L, DateUtils.FORMAT_ABBREV_ALL));

            offerViewHolder.status.setText(offer.getStatus() == Offer.OfferStatus.ENTERED ? "abierto" : "cerrado");
            offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                    offer.getStatus() == Offer.OfferStatus.ENTERED ? R.color.colorGreenText : R.color.colorRed),
                    PorterDuff.Mode.SRC);

            if(offer.isApplied()){
                if(offer.getApplication().getApplicationStatus() == REJECTED){
                    offerViewHolder.status.setText("rechazada");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorRed),
                            PorterDuff.Mode.SRC);
                } else if (offer.getApplication().getApplicationStatus() == CANCELED){
                    offerViewHolder.status.setText("cancelada");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorRed),
                            PorterDuff.Mode.SRC);
                } else if (offer.getApplication().getApplicationStatus() == REVISION){
                    offerViewHolder.status.setText("en revisión");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorAcademicShape),
                            PorterDuff.Mode.SRC);
                } else if (offer.getApplication().getApplicationStatus() == ACCEPTED){
                    offerViewHolder.status.setText("aceptada");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.green),
                            PorterDuff.Mode.SRC);
                }
            }
            offerViewHolder.title.setText(offer.getTitle());
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
            otherSymbols.setDecimalSeparator(',');
            otherSymbols.setGroupingSeparator('.');
            offerViewHolder.amount.setText(String.format("$%s", new DecimalFormat("#,###", otherSymbols).format(offer.getTotalWage())));
            offerViewHolder.description.setText(offer.getSummary());
            offerViewHolder.details.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onOfferSelected(offer);
                }
            });

        } else if(holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        } else if (holder instanceof NotificationViewHolder) {
            NotificationViewHolder notificationViewHolder = (NotificationViewHolder)holder;
            final NotificationResume notification = (NotificationResume) offers.get(position);
            notificationViewHolder.notification.setText(notification.getResume());
            notificationViewHolder.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    offers.remove(position);
                    notifyItemRemoved(position);
                    listener.closeNotifications();
                }
            });
            notificationViewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.closeNotifications();
                    listener.onNotificationSelected();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return offers == null ? 0 : offers.size();
    }

    class OfferViewHolder extends RecyclerView.ViewHolder {
        public TextView resume;
        public TextView date;
        public Button status;
        public ImageView image;
        public TextView title;
        public TextView amount;
        public TextView description;
        public TextView details;
        public View layout;

        OfferViewHolder(View view) {
            super(view);
            layout = view;
            resume = view.findViewById(R.id.publication_resume);
            date = view.findViewById(R.id.creationDate);
            status = view.findViewById(R.id.button);
            image = view.findViewById(R.id.picture);
            title = view.findViewById(R.id.title);
            amount = view.findViewById(R.id.totalWage);
            description = view.findViewById(R.id.description);
            details = view.findViewById(R.id.details);
        }
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;

        LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progressBar1);
        }
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notification;
        ImageView close;
        View view;

        NotificationViewHolder(View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification);
            close = itemView.findViewById(R.id.close_btn);
            this.view = itemView;
        }
    }

    public interface AdapterListener{
        void onOfferSelected(Offer offer);
        void onLoadMoreOffers(int page);
        void closeNotifications();
        void onNotificationSelected();
    }

    /*public enum RowType {
        LIST_ITEM(1), HEADER_ITEM(2);

        private int _value;

        RowType(int Value) {
            this._value = Value;
        }

        public int getValue() {
            return _value;
        }

        @Nullable
        public static RowType fromInt(int i) {
            for (RowType b : RowType.values()) {
                if (b.getValue() == i) { return b; }
            }
            return null;
        }
    }*/

}
