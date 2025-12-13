using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
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
    public sealed partial class CommentsPage : Page
    {
        bool iserror;

        public CommentsPage()
        {
            this.InitializeComponent();
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
            if(!iserror)
            {
                webView1.Navigate(new Uri("ms-appx-web:///Assets/WebPages/Error.html"));
                iserror = true;
            }
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

        public async void webView1_LoadCompleted(object sender, NavigationEventArgs e)
        {
            progress1.IsActive = false;
            progress.IsIndeterminate = false;
            progress.Visibility = Visibility.Collapsed;

            string functionString = @"document.querySelector('.plugin').style.overflow = 'visible';
                var msViewportStyle = document.createElement('style');
                msViewportStyle.appendChild(document.createTextNode('@-ms-viewport{width:device-width!important;}'));
                document.getElementsByTagName('head')[0].appendChild(msViewportStyle);";
            if(!iserror)
            {
                await webView1.InvokeScriptAsync("eval", new string[] { functionString });
                iserror = false;
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
            if(webView1.CanGoForward)
            {
                webView1.GoForward();
            }
        }

        public void CBRefresh_Click(object sender, RoutedEventArgs e)
        {
            webView1.Refresh();
        }
    }
}
