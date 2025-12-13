using SQLitePCL;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.ApplicationModel.DataTransfer;
using Windows.Data.Json;
using Windows.Foundation;
using Windows.Foundation.Collections;
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
using ZeldaComBr.Models;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace ZeldaComBr
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class Post : Page
    {
        PostItem post;

        public Post()
        {
            Other.Other.Transition(this);
            this.InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            post = e.Parameter as PostItem;

            int contains = 0;
            string SQL = @"SELECT * FROM favorites WHERE url='" + post.Url + "';";
            var iffavorite = Other.Other.objConn.Prepare(SQL);

            while (iffavorite.Step() == SQLiteResult.ROW)
            {
                Debug.WriteLine(contains);
                contains++;
            }

            if (contains > 0)
            {
                CBFavorite.Icon = new SymbolIcon(Symbol.UnFavorite);
                CBFavorite.Label = "Desmarcar favorito";
            }

            FormPost();
        }

        public void FormPost()
        {
            string content = post.Post.Replace("(apos)", "'");
            
            PostWebView.NavigateToString(content);
            PostWebView.NavigationStarting += PostWebView_NavigationStarting;
        }

        public void PostWebView_NavigationStarting(WebView sender, WebViewNavigationStartingEventArgs args)
        {
            string url = args.Uri.ToString();

            if (url.Contains("apps.aloogle.net"))
            {
                if (url.Contains("image"))
                {
                    Frame.Navigate(typeof(ImageZoom), Other.Other.getQuery(url, "image"));
                }
            }
            else
            {
                OpenBrowser(url);
            }

            args.Cancel = true;
        }

        private void CBFavorite_Click(object sender, RoutedEventArgs e)
        {
            ToolTip toolTip = new ToolTip();
            string label = "";
            if (CBFavorite.Label.Equals("Marcar como favorito"))
            {
                Other.Other.objConn.Prepare(@"INSERT INTO favorites (title, description, post, image, url, comments, date, author) VALUES ('" + post.Title.Replace("'", "(apos)") + "', '" + post.Description.Replace("'", "(apos)") + "', '" + post.Post + "', '" + post.Image + "', '" + post.Url + "', '" + post.Comments + "', '" + post.Date + "', '" + post.Author + "');").Step();
                CBFavorite.Icon = new SymbolIcon(Symbol.UnFavorite);
                label = "Desmarcar favorito";
            }
            else
            {
                Other.Other.objConn.Prepare(@"DELETE FROM favorites WHERE url='" + post.Url + "';").Step();
                CBFavorite.Icon = new SymbolIcon(Symbol.Favorite);
                label = "Marcar como favorito";
            }
            CBFavorite.Label = label;
            toolTip.Content = label;
            ToolTipService.SetToolTip(CBFavorite, toolTip);
            Favorites.LoadPosts();
        }

        private void CBShare_Click(object sender, RoutedEventArgs e)
        {
            DataTransferManager datatransfermanager = DataTransferManager.GetForCurrentView();
            datatransfermanager.DataRequested += new TypedEventHandler<DataTransferManager, DataRequestedEventArgs>(ShareTextHandler);
            DataTransferManager.ShowShareUI();
        }

        private void CBCopyLink_Click(object sender, RoutedEventArgs e)
        {
            DataPackage dataPackage = new DataPackage();
            dataPackage.RequestedOperation = DataPackageOperation.Copy;
            dataPackage.SetText(post.Url);
            Clipboard.SetContent(dataPackage);
        }

        private void CBOpenSite_Click(object sender, RoutedEventArgs e)
        {
            OpenBrowser(post.Url);
        }

        public async void OpenBrowser(string url)
        {
            await Launcher.LaunchUriAsync(new Uri(url));
        }

        private async void ShareTextHandler(DataTransferManager sender, DataRequestedEventArgs e)
        {
            DataRequest request = e.Request;

            request.Data.Properties.Title = "Compartilhar link";
            request.Data.Properties.Description = Convert.ToString(post.Url);

            try
            {
                String text = post.Title + " " + Convert.ToString(post.Url);
                request.Data.SetText(text);
            }
            catch
            {
                MessageDialog dialog = new MessageDialog("Houve um erro.");
                await dialog.ShowAsync();
            }
        }

        private void CBComments_Click(object sender, RoutedEventArgs e)
        {
            string[] info = { "Comentários: " + post.Title, "https://www.facebook.com/plugins/comments.php?href=" + post.Url };
            Frame.Navigate(typeof(CommentsPage), info);
        }
    }
}