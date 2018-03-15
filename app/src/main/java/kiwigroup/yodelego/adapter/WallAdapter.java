package kiwigroup.yodelego.adapter;

import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.format.DateUtils;
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
import kiwigroup.yodelego.model.Offer;

/**
 * Created by cristian on 4/20/17.
 */

public class WallAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private AdapterListener listener;
    private List<Offer> offers;

    public WallAdapter(AdapterListener listener) {
        this.listener = listener;
        offers = new ArrayList<>();
    }

    public void append(Offer item) {
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

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_ITEM) {
            View v = inflater.inflate(R.layout.content_wall_item, parent, false);
            return new OfferViewHolder(v);
        } else if (viewType == VIEW_TYPE_LOADING) {
            View v = inflater.inflate(R.layout.content_item_loading, parent, false);
            return new LoadingViewHolder(v);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return offers.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof OfferViewHolder) {
            OfferViewHolder offerViewHolder = (OfferViewHolder)holder;
            final Offer offer = offers.get(position);
            offerViewHolder.resume.setText(Html.fromHtml("<b>" + offer.getPublisher() + "</b>  publicó un nuevo trabajo"));
            offerViewHolder.date.setText(DateUtils.getRelativeTimeSpanString(offer.getDate().getTime(), new Date().getTime(),0L, DateUtils.FORMAT_ABBREV_ALL));
            offerViewHolder.status.setText(offer.getStatus() == Offer.OfferStatus.ENTERED ? "abierto" : "cerrado");
            offerViewHolder.status.getBackground().setColorFilter(ContextCompat.getColor(offerViewHolder.status.getContext(),
                    offer.getStatus() == Offer.OfferStatus.ENTERED ? R.color.colorGreenText : R.color.colorRed),
                    PorterDuff.Mode.SRC);
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

        public OfferViewHolder(View view) {
            super(view);
            layout = view;
            resume = view.findViewById(R.id.publication_resume);
            date = view.findViewById(R.id.date);
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

    public interface AdapterListener{
        void onOfferSelected(Offer offer);
        void onLoadMoreOffers(int page);
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
