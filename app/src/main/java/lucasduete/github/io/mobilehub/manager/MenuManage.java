package lucasduete.github.io.mobilehub.manager;

import android.content.Context;
import android.content.Intent;
import android.view.MenuItem;

import lucasduete.github.io.mobilehub.ListRepositoriesActivity;
import lucasduete.github.io.mobilehub.ListStarsActivity;
import lucasduete.github.io.mobilehub.MainActivity;
import lucasduete.github.io.mobilehub.ProfileActivity;
import lucasduete.github.io.mobilehub.R;
import lucasduete.github.io.mobilehub.SearchActivity;
import lucasduete.github.io.mobilehub.SettingActivity;

public class MenuManage {

    public static Intent getActivity(Context context, MenuItem item) {
        int id = item.getItemId();
        Intent intent = null;

        switch (id) {
            case R.id.nav_inicio:
                intent = new Intent(context, MainActivity.class);
                break;
            case R.id.nav_perfil:
                intent = new Intent(context, ProfileActivity.class);
                break;
            case R.id.nav_meusRepositorios:
                intent = new Intent(context, ListRepositoriesActivity.class);
                break;
            case R.id.nav_minhasEstrelas:
                intent = new Intent(context, ListStarsActivity.class);
                break;
            case R.id.nav_buscar:
                intent = new Intent(context, SearchActivity.class);
                break;
        }

//            RF Removido
//            case R.id.nav_settings:
//                intent = new Intent(context, SettingActivity.class);
//                break;

        return intent;
    }
}
