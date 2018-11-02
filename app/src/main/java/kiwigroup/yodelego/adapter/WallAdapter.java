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

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import kiwigroup.yodelego.R;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.model.StatusNotification;
import kiwigroup.yodelego.model.WallItem;

import static kiwigroup.yodelego.model.Application.ApplicationStatus.ACCEPTED;
import static kiwigroup.yodelego.model.Application.ApplicationStatus.CANCELED_BY_APPLICANT;
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

    public void updateStatusNotification(List<StatusNotification> statusNotification){
        /*if(offers.size() == 0){
            offers.add(notificationResume);
        } else {
            if(offers.get(0) instanceof StatusNotification){
                offers.set(0, notificationResume);
            } else {
                offers.add(0, notificationResume);
            }
        }
        notifyItemInserted(0);*/
        Iterator<WallItem> iter = offers.iterator();
        while (iter.hasNext()) {
            WallItem item = iter.next();
            if (item instanceof StatusNotification)
                iter.remove();
        }

        offers.addAll(0, statusNotification);
        notifyDataSetChanged();
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
            final OfferViewHolder offerViewHolder = (OfferViewHolder)holder;
            final Offer offer = (Offer) offers.get(position);
            offerViewHolder.resume.setText(Html.fromHtml("<b>" + offer.getPublisher().getName() + "</b>  publicó un nuevo trabajo"));
            offerViewHolder.date.setText(DateUtils.getRelativeTimeSpanString(offer.getCreationDate().getTime(), new Date().getTime(),0L, DateUtils.FORMAT_ABBREV_ALL));

            /*offerViewHolder.status.setText(offer.getKind() == Offer.OfferStatus.ENTERED ? "abierto" : "cerrado");
            offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                    offer.getKind() == Offer.OfferStatus.ENTERED ? R.color.colorGreenText : R.color.colorRed),
                    PorterDuff.Mode.SRC);
            if(offer.getImages() != null && offer.getImages().size() > 0){
                offerViewHolder.image.setVisibility(View.VISIBLE);
                Picasso.get().load(offer.getImages().get(0)).into(offerViewHolder.image);
            } else {
                offerViewHolder.image.setVisibility(View.GONE);
            }*/

            if(offer.getImages() != null && offer.getImages().size() > 0){
                offerViewHolder.image.setVisibility(View.VISIBLE);
                Picasso.get().load(offer.getImages().get(0)).into(offerViewHolder.image);
            } else {
                offerViewHolder.image.setVisibility(View.GONE);
            }

            if(offer.getPublisher().getProfilePictureUrl() != null && !offer.getPublisher().getProfilePictureUrl().isEmpty()){
                Picasso.get().load(offer.getPublisher().getProfilePictureUrl()).into(offerViewHolder.profileImage);
            }

            if(!offer.isAppliedByMe()){
                Log.d("WallAdapter", "**** pos: " + position + " hasStarted: " + offer.hasStarted());

                if (offer.getStatus() == Offer.OfferStatus.ENTERED ||
                        offer.getStatus() == Offer.OfferStatus.REVISION ||
                        offer.getStatus() == Offer.OfferStatus.ACCEPTED_APPLICATION){
                    if(offer.hasStarted()){
                        offerViewHolder.status.setText("cerrado");
                        offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                R.color.colorRed),
                                PorterDuff.Mode.SRC);
                    } else {
                        offerViewHolder.status.setText("abierto");
                        offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                R.color.colorGreenText),
                                PorterDuff.Mode.SRC);
                    }
                } else if (offer.getStatus() == Offer.OfferStatus.FILLED ){
                    if(offer.hasStarted()){
                        offerViewHolder.status.setText("cerrado");
                        offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                R.color.colorRed),
                                PorterDuff.Mode.SRC);
                    } else {
                        if(offer.isPaid()){
                            offerViewHolder.status.setText("adjudicado");
                            offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                    R.color.colorAdjudicated),
                                    PorterDuff.Mode.SRC);
                        } else {
                            offerViewHolder.status.setText("sin vacantes");
                            offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                    R.color.colorGreyButton),
                                    PorterDuff.Mode.SRC);
                            offerViewHolder.status.setTextColor(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                    R.color.colorPrimaryDark));
                        }
                    }
                } else if(offer.getStatus() == Offer.OfferStatus.CLOSED ){
                    offerViewHolder.status.setText("cerrado");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorRed),
                            PorterDuff.Mode.SRC);
                } else if(offer.getStatus() == Offer.OfferStatus.CANCELED ){
                    offerViewHolder.status.setText("cancelado");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorRed),
                            PorterDuff.Mode.SRC);
                } else if (offer.getStatus() == Offer.OfferStatus.PAUSED ){
                    if(offer.hasStarted()){
                        offerViewHolder.status.setText("cerrado");
                        offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                R.color.colorRed),
                                PorterDuff.Mode.SRC);
                    } else {
                        offerViewHolder.status.setText("pausado");
                        offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                R.color.colorGreyButton),
                                PorterDuff.Mode.SRC);
                        offerViewHolder.status.setTextColor(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                R.color.colorPrimaryDark));
                    }

                } else if(offer.getStatus() == Offer.OfferStatus.DEACTIVATED ){
                    offerViewHolder.status.setText("desactivado");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorRed),
                            PorterDuff.Mode.SRC);
                }

                /*if(offer.hasStarted()){
                    offerViewHolder.status.setText("cerrado");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorRed),
                            PorterDuff.Mode.SRC);
                }*/

            } else {
                if(offer.getApplication().getApplicationStatus() == REJECTED){
                    offerViewHolder.status.setText("no califica");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorRed),
                            PorterDuff.Mode.SRC);
                } else if (offer.getApplication().getApplicationStatus() == CANCELED_BY_APPLICANT){
                    offerViewHolder.status.setText("cancelado");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorRed),
                            PorterDuff.Mode.SRC);
                } else if (offer.getApplication().getApplicationStatus() == CANCELED_BY_APPLICANT){
                    offerViewHolder.status.setText("cancelado");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorRed),
                            PorterDuff.Mode.SRC);
                } else if (offer.getApplication().getApplicationStatus() == REVISION){
                    if(offer.hasStarted()){
                        offerViewHolder.status.setText("cerrado");
                        offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                R.color.colorRed),
                                PorterDuff.Mode.SRC);
                    } else {
                        offerViewHolder.status.setText("en revisión");
                        offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                R.color.colorAcademicShape),
                                PorterDuff.Mode.SRC);
                    }
                } else if (offer.getApplication().getApplicationStatus() == ACCEPTED){
                    if(offer.isPaid()) {
                        if(offer.hasFinished()){
                            if(!offer.getApplication().isClosed()) {
                                offerViewHolder.status.setText("completado");
                                offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                        R.color.colorPrimaryDark),
                                        PorterDuff.Mode.SRC);
                            } else {
                                offerViewHolder.status.setText("cerrado");
                                offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                        R.color.colorRed),
                                        PorterDuff.Mode.SRC);
                            }
                        } else {
                            offerViewHolder.status.setText("adjudicado");
                            offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                    R.color.colorAdjudicated),
                                    PorterDuff.Mode.SRC);
                        }
                    } else {
                        if(offer.getApplication().isClosed()) {
                            offerViewHolder.status.setText("cerrado");
                            offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                    R.color.colorRed),
                                    PorterDuff.Mode.SRC);
                        } else {
                            offerViewHolder.status.setText("en revisión");
                            offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                                    R.color.colorAcademicShape),
                                    PorterDuff.Mode.SRC);
                        }
                    }
                }

                /*if(offer.getApplication().isClosed()){
                    offerViewHolder.status.setText("cerrado");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorRed),
                            PorterDuff.Mode.SRC);
                } else if(offer.getApplication().isQualifiable()){
                    offerViewHolder.status.setText("completado");
                    offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                            R.color.colorPrimaryDark),
                            PorterDuff.Mode.SRC);
                }*/
            }
            offerViewHolder.title.setText(offer.getTitle());
            DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(new Locale("es", "ES"));
            otherSymbols.setDecimalSeparator(',');
            otherSymbols.setGroupingSeparator('.');
            offerViewHolder.amount.setText(String.format("$%s", new DecimalFormat("#,###", otherSymbols).format(offer.getWage())));
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
            final StatusNotification notification = (StatusNotification) offers.get(position);
            if(notification.getKind() == StatusNotification.NotificationKinds.OFFER_AVAILABLE){
                notificationViewHolder.notification.setText("Existe una nueva oferta disponible");
                notificationViewHolder.icon.setVisibility(View.INVISIBLE);
            } else if (notification.getKind() == StatusNotification.NotificationKinds.APPLICATION_ACCEPTED){
                notificationViewHolder.notification.setText(String.format("Tu postulación a %s ha sido aceptada", notification.getOffer()));
            } else if (notification.getKind() == StatusNotification.NotificationKinds.APPLICATION_REJECTED){
                notificationViewHolder.notification.setText(String.format("Tu postulación a %s ha sido rechazada", notification.getOffer()));
            } else if (notification.getKind() == StatusNotification.NotificationKinds.APPLICATION_CANCELED_BY_APPLICANT){
                notificationViewHolder.notification.setText(String.format("Tu postulación a %s ha sido cancelada con éxito", notification.getOffer()));
                notificationViewHolder.icon.setVisibility(View.INVISIBLE);
            } else if (notification.getKind() == StatusNotification.NotificationKinds.OFFER_CANCELED){
                notificationViewHolder.notification.setText(String.format("La oferta %s ha sido cancelada", notification.getOffer()));
                notificationViewHolder.icon.setVisibility(View.INVISIBLE);
            }
            notificationViewHolder.close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    offers.remove(position);
                    notifyItemRemoved(position);
                    listener.closeNotification(notification);
                }
            });
            notificationViewHolder.icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.closeNotification(notification);
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
        public CircleImageView profileImage;

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
            profileImage = view.findViewById(R.id.profile_image);
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
        ImageView icon;
        View view;

        NotificationViewHolder(View itemView) {
            super(itemView);
            notification = itemView.findViewById(R.id.notification);
            close = itemView.findViewById(R.id.close_btn);
            icon = itemView.findViewById(R.id.schedule_icon);
            this.view = itemView;
        }
    }

    public interface AdapterListener{
        void onOfferSelected(Offer offer);
        void onLoadMoreOffers(int page);
        void closeNotification(StatusNotification notification);
        void onNotificationSelected();
    }

}
