namespace ec.edu.monster
{
    public static class MauiProgram
    {
        public static MauiApp CreateMauiApp()
        {
            MauiAppBuilder constructor = MauiApp.CreateBuilder();
            constructor
                .UseMauiApp<App>()
                .ConfigureFonts(fuentes =>
                {
                    fuentes.AddFont("OpenSans-Regular.ttf", "OpenSansRegular");
                    fuentes.AddFont("OpenSans-Semibold.ttf", "OpenSansSemibold");
                });

            // Los campos ya van dentro de un borde redondeado: se quita el subrayado nativo
            Microsoft.Maui.Handlers.EntryHandler.Mapper.AppendToMapping("SinSubrayado", (manejador, _) =>
            {
#if ANDROID
                manejador.PlatformView.BackgroundTintList =
                        Android.Content.Res.ColorStateList.ValueOf(Android.Graphics.Color.Transparent);
#elif WINDOWS
                manejador.PlatformView.BorderThickness = new Microsoft.UI.Xaml.Thickness(0);
#endif
            });

            return constructor.Build();
        }
    }
}