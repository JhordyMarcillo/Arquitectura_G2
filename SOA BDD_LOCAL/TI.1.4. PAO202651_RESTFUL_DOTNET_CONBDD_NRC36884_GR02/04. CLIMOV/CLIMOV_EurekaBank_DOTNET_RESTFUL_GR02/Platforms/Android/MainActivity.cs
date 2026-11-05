using Android.App;
using Android.Content.PM;
using Android.OS;

namespace ec.edu.monster;

[Activity(Theme = "@style/Maui.SplashTheme", MainLauncher = true, LaunchMode = LaunchMode.SingleTop, ConfigurationChanges = ConfigChanges.ScreenSize | ConfigChanges.Orientation | ConfigChanges.UiMode | ConfigChanges.ScreenLayout | ConfigChanges.SmallestScreenSize | ConfigChanges.Density)]
public class MainActivity : MauiAppCompatActivity
{
    /// <summary>URL del servidor enviada al abrir la app (adb shell am start --es urlServidor ...).</summary>
    public const string EXTRA_URL = "urlServidor";

    protected override void OnCreate(Bundle? savedInstanceState)
    {
        string? url = Intent?.GetStringExtra(EXTRA_URL);
        if (!string.IsNullOrWhiteSpace(url))
        {
            App.Servicio.UrlServidor = url.Trim();
            App.GuardarUrlServidor();
        }
        base.OnCreate(savedInstanceState);
    }
}
