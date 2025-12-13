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
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace DropandoIdeias
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class ImageZoom : Page
    {
        string url, imagetitle;

        public ImageZoom()
        {
            this.InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            url = WebUtility.UrlDecode(e.Parameter as string);
            string[] image = url.Split('/');
            imagetitle = url.Split('/')[image.Length - 1];

            CBTitle.Text = imagetitle;

            ZoomImage.Source = new BitmapImage(new Uri(url, UriKind.Absolute));
        }

        private void CBDownload_Click(object sender, RoutedEventArgs e)
        {
            DownloadImage();
        }

        public async void DownloadImage()
        {
            Uri uri = new Uri(url);

            // download image from uri into temp storagefile
            var file = await StorageFile.CreateStreamedFileFromUriAsync(imagetitle, uri, RandomAccessStreamReference.CreateFromUri(uri));

            // file is readonly, copy to a new location to remove restrictions
            var file2 = await file.CopyAsync(KnownFolders.PicturesLibrary);

            Other.Other.ShowMessage("Imagem salva na pasta Imagens");
        }

        private void CBShare_Click(object sender, RoutedEventArgs e)
        {
            DataTransferManager datatransfermanager = DataTransferManager.GetForCurrentView();
            datatransfermanager.DataRequested += new TypedEventHandler<DataTransferManager, DataRequestedEventArgs>(ShareTextHandler);
            DataTransferManager.ShowShareUI();
        }

        private void Back_Click(object sender, RoutedEventArgs e)
        {
            Frame.GoBack();
        }

        private async void ShareTextHandler(DataTransferManager sender, DataRequestedEventArgs e)
        {
            DataRequest request = e.Request;

            request.Data.Properties.Title = "Compartilhar link";
            request.Data.Properties.Description = Convert.ToString(url);

            try
            {
                String text = imagetitle + " " + Convert.ToString(url);
                request.Data.SetText(text);
            }
            catch
            {
                MessageDialog dialog = new MessageDialog("Houve um erro.");
                await dialog.ShowAsync();
            }
        }
    }
}
