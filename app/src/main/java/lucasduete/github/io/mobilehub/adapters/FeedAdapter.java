package lucasduete.github.io.mobilehub.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import lucasduete.github.io.mobilehub.R;
import lucasduete.github.io.mobilehub.RepositoryActivity;
import lucasduete.github.io.mobilehub.models.Feed;

public class FeedAdapter extends BaseAdapter {

    private List<Feed> feedList;
    private Activity activity;

    public FeedAdapter(List<Feed> feedList, Activity activity) {
        this.feedList = feedList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return feedList.size();
    }

    @Override
    public Feed getItem(int position) {
        return feedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = activity.getLayoutInflater().inflate(R.layout.feed_list, parent, false);
        Feed feed = feedList.get(position);

        TextView textView = view.findViewById(R.id.textViewFeedTitle);
        textView.setText(feed.getTitle());

        ImageView imageView = view.findViewById(R.id.imageViewFeedAvatarUrl);
        Picasso.get().load(feed.getAvatarUrl()).into(imageView);

        view.setOnClickListener(localView -> {
            Intent intent = new Intent(view.getContext(), RepositoryActivity.class);

            String[] split = feed.getTitle().split(" ");
            String repoName = split[split.length -1];
            String repoOwner = repoName.split("/")[0];

            intent.putExtra("repoName", repoName);
            intent.putExtra("repoOwner", repoOwner);

            view.getContext().startActivity(intent);
        });

        return view;
    }
}
