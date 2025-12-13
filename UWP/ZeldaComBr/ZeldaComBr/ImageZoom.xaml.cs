using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.ApplicationModel.DataTransfer;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.Storage;
using Windows.Storage.Streams;
using Windows.System;
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;
using Windows.UI.Xaml.Navigation;
using XamlAnimatedGif;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace ZeldaComBr
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class ImageZoom : Page
    {
        string url, imageTitle;
        StorageFile file2;

        public ImageZoom()
        {
            this.InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            url = WebUtility.UrlDecode(e.Parameter as string);
            string[] image = url.Split('/');
            imageTitle = url.Split('/')[image.Length - 1];

            CBTitle.Text = imageTitle;

            AnimationBehavior.SetSourceUri(ZoomImage, new Uri(url, UriKind.Absolute));
        }

        private async void CBDownload_Click(object sender, RoutedEventArgs e)
        {
            Loading.Visibility = Visibility.Visible;
            try
            {
                Uri uri = new Uri(url);
                var file = await StorageFile.CreateStreamedFileFromUriAsync(imageTitle, uri, RandomAccessStreamReference.CreateFromUri(uri));
                file2 = await file.CopyAsync(KnownFolders.PicturesLibrary, imageTitle, NameCollisionOption.ReplaceExisting);

                MessageDialog md = new MessageDialog(imageTitle + " salvo na pasta imagens" + KnownFolders.PicturesLibrary.Path);

                md.Commands.Add(new UICommand("Abrir", new UICommandInvokedHandler(CommandHandlers)) { Id = 0 });
                md.Commands.Add(new UICommand("Fechar", new UICommandInvokedHandler(CommandHandlers)) { Id = 1 });

                await md.ShowAsync();
            }
            catch (Exception ex)
            {
                Other.Other.ShowMessage("Erro ao salvar imagem");
            }
            Loading.Visibility = Visibility.Collapsed;
        }

        public async void CommandHandlers(IUICommand commandLabel)
        {
            var Actions = commandLabel.Label;
            switch (Actions)
            {
                case "Abrir":
                    await Launcher.LaunchFileAsync(file2);
                    break;
            }
        }

        private void CBShare_Click(object sender, RoutedEventArgs e)
        {
            DataTransferManager datatransfermanager = DataTransferManager.GetForCurrentView();
            datatransfermanager.DataRequested += new TypedEventHandler<DataTransferManager, DataRequestedEventArgs>(DataTransferManager_DataRequested);
            DataTransferManager.ShowShareUI();
        }

        private async void DataTransferManager_DataRequested(DataTransferManager sender, DataRequestedEventArgs args)
        {
            DataRequestDeferral deferral = args.Request.GetDeferral();
            args.Request.Data.Properties.Title = "Compartilhar imagem";
            args.Request.Data.Properties.Description = url;
            args.Request.Data.SetText(url + " - Compartilhado pelo Zelda.com.br para Windows 10");

            if (!File.Exists("ms-appdata:///temp/" + imageTitle))
            {
                try
                {
                    Uri uri = new Uri(url);
                    var file = await StorageFile.CreateStreamedFileFromUriAsync(imageTitle, uri, RandomAccessStreamReference.CreateFromUri(uri));
                    var file2 = await file.CopyAsync(ApplicationData.Current.TemporaryFolder);
                }
                catch (Exception e)
                {
                    return;
                }
            }
            var imageUri = "ms-appdata:///temp/" + imageTitle;

            StorageFile imageFile = await StorageFile.GetFileFromApplicationUriAsync(new Uri(imageUri));

            List<IStorageItem> imageItems = new List<IStorageItem>();
            imageItems.Add(imageFile);
            args.Request.Data.SetStorageItems(imageItems);

            RandomAccessStreamReference imageStreamRef = RandomAccessStreamReference.CreateFromFile(imageFile);
            args.Request.Data.Properties.Thumbnail = imageStreamRef;
            args.Request.Data.SetBitmap(imageStreamRef);

            deferral.Complete();
        }
    }
}