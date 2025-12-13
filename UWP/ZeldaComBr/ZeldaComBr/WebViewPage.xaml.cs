using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading.Tasks;
using Windows.ApplicationModel.DataTransfer;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.System;
using Windows.UI.Core;
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace ZeldaComBr
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class WebViewPage : Page
    {
        public static WebView WV;
        public static TextBlock title;

        public WebViewPage()
        {
            this.InitializeComponent();
            WV = webView1;
            title = CBTitle;
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            string[] itens = e.Parameter as string[];

            CBTitle.Text = itens[0];

            webView1.Navigate(new Uri(itens[1]));
            webView1.ContentLoading += webView1_ContentLoading;
            webView1.NavigationStarting += webView1_NavigationStarting;
            webView1.LoadCompleted += webView1_LoadCompleted;
            webView1.NavigationFailed += WebView1_NavigationFailed;
        }

        private void WebView1_NavigationFailed(object sender, WebViewNavigationFailedEventArgs e)
        {
            webView1.Navigate(new Uri("ms-appx-web:///WebPages/Error.html"));
        }

        private void webView1_ContentLoading(WebView sender, WebViewContentLoadingEventArgs args)
        {
            CBBack.IsEnabled = webView1.CanGoForward;
            CBForward.IsEnabled = webView1.CanGoBack;

            progress1.IsActive = true;
            progress.IsIndeterminate = true;
        }

        public void webView1_NavigationStarting(WebView sender, WebViewNavigationStartingEventArgs args)
        {
            progress1.IsActive = true;
            progress.IsIndeterminate = true;
            progress.Visibility = Visibility.Visible;

            CBForward.IsEnabled = false;
        }

        public void webView1_LoadCompleted(object sender, NavigationEventArgs e)
        {
            progress1.IsActive = false;
            progress.IsIndeterminate = false;
            progress.Visibility = Visibility.Collapsed;

            if (webView1.DocumentTitle.ToString().Equals(""))
            {
                CBTitle.Text = "Hyrule Legends - Zelda.com.br";
            }
            else
            {
                CBTitle.Text = webView1.DocumentTitle.ToString();
            }
        }

        private async void ShareTextHandler(DataTransferManager sender, DataRequestedEventArgs e)
        {
            DataRequest request = e.Request;

            request.Data.Properties.Title = "Compartilhar link";
            request.Data.Properties.Description = Convert.ToString(webView1.Source);

            try
            {
                String text = webView1.DocumentTitle + " " + Convert.ToString(webView1.Source);
                request.Data.SetText(text);
            }
            catch
            {
                MessageDialog dialog = new MessageDialog("Houve um erro.");
                await dialog.ShowAsync();
            }
        }
        private void ShareButton_Click(object sender, RoutedEventArgs e)
        {
            if(webView1.Source.ToString().StartsWith("http"))
            {
                DataTransferManager datatransfermanager = DataTransferManager.GetForCurrentView();
                datatransfermanager.DataRequested += new TypedEventHandler<DataTransferManager, DataRequestedEventArgs>(ShareTextHandler);
                DataTransferManager.ShowShareUI();
            }
        }

        public void CBBack_Click(object sender, RoutedEventArgs e)
        {
            if(webView1.CanGoBack)
            {
                webView1.GoBack();
            }
        }

        public void CBForward_Click(object sender, RoutedEventArgs e)
        {
            if (webView1.CanGoForward)
            {
                webView1.GoForward();
            }
        }

        public void CBRefresh_Click(object sender, RoutedEventArgs e)
        {
            webView1.Refresh();
        }

        private void SearchBox_QuerySubmitted(AutoSuggestBox sender, AutoSuggestBoxQuerySubmittedEventArgs args)
        {
            CBTitle.Text = "Busca: " + args.QueryText;

            Uri targetUri = new Uri("http://www.zelda.com.br/search/node/" + args.QueryText);
            webView1.Navigate(targetUri);
        }

        private void CloseSearchHolder_Click(object sender, RoutedEventArgs e)
        {
            SearchHolder.Visibility = Visibility.Collapsed;
        }

        private void SearchButton_Click(object sender, RoutedEventArgs e)
        {
            if (SearchHolder.Visibility == Visibility.Collapsed)
            {
                SearchHolder.Visibility = Visibility.Visible;
                Task.Factory.StartNew(() => Dispatcher.RunAsync(CoreDispatcherPriority.Low, () => SearchBox.Focus(FocusState.Programmatic)));
            }
            else
            {
                SearchHolder.Visibility = Visibility.Collapsed;
            }
        }

        private void CBOpen_Click(object sender, RoutedEventArgs e)
        {
            if (webView1.Source.ToString().StartsWith("http"))
            {
                OpenBrowser();
            }
        }

        public async void OpenBrowser()
        {
            await Launcher.LaunchUriAsync(new Uri(webView1.Source.ToString()));
        }
    }
}
