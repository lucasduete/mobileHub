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

import lucasduete.github.io.mobilehub.IssueActivity;
import lucasduete.github.io.mobilehub.R;
import lucasduete.github.io.mobilehub.models.Issue;
import lucasduete.github.io.mobilehub.models.Repository;

public class IssueAdapter extends BaseAdapter {

    private final List<Issue> issues;
    private final Activity activity;

    public IssueAdapter(List<Issue> issues, Activity activity) {
        this.issues = issues;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return issues.size();
    }

    @Override
    public Object getItem(int position) {
        return issues.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = activity.getLayoutInflater().inflate(R.layout.issues_list, parent, false);
        Issue issue = issues.get(position);

        TextView textView;

        textView = (TextView) view.findViewById(R.id.textViewNomeIssue);
        textView.setText(issue.getNome());

        textView = (TextView) view.findViewById(R.id.textViewNumeroIssues);
        textView.setText(issue.getNumero());

        textView = (TextView) view.findViewById(R.id.textViewNomeAutor);
        textView.setText(issue.getNomeAutor());

        ImageView imageView = (ImageView) view.findViewById(R.id.imageViewFotoAutor);
        Picasso.get().load(issue.getFotoAutor()).into(imageView);

        view.setOnClickListener(localView -> {
            Intent intent = new Intent(view.getContext(), IssueActivity.class);
            view.getContext().startActivity(intent);
        });

        return view;
    }
}
