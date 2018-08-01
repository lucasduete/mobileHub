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
import lucasduete.github.io.mobilehub.models.Repository;

public class RepositoryAdapter extends BaseAdapter {

    private final List<Repository> Repositorys;
    private final Activity activity;

    public RepositoryAdapter(List<Repository> Repositorys, Activity activity) {
        this.Repositorys = Repositorys;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return Repositorys.size();
    }

    @Override
    public Object getItem(int position) {
        return Repositorys.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = activity.getLayoutInflater().inflate(R.layout.repos_list, parent, false);
        Repository repository = Repositorys.get(position);

        TextView textView;

        textView = view.findViewById(R.id.textViewNameRepo);
        textView.setText(repository .getNome());

        textView = view.findViewById(R.id.textViewNameAuthor);
        textView.setText(repository .getNomeAutor());

        textView = view.findViewById(R.id.textViewDescRepo);
        if (repository.getDescricao().equalsIgnoreCase("null"))
            textView.setText("(Nenhuma Descrição Fornecida)");
        else
            textView.setText(repository.getDescricao());

        ImageView imageView = view.findViewById(R.id.imageViewFotoRepo);
        Picasso.get().load(repository .getFoto()).into(imageView);

        view.setOnClickListener(localView -> {
            Intent intent = new Intent(view.getContext(), RepositoryActivity.class);

            intent.putExtra("repoName", repository.getNome());
            intent.putExtra("repoOwner", repository.getNomeAutor());

            view.getContext().startActivity(intent);
        });

        return view;
    }


}
