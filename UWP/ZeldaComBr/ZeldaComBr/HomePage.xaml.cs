using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading.Tasks;
using Windows.Data.Json;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.Storage;
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
    public sealed partial class HomePage : Page
    {
        public static Frame post, posts;
        DispatcherTimer tmr = new DispatcherTimer();

        public HomePage()
        {
            this.InitializeComponent();
            this.NavigationCacheMode = NavigationCacheMode.Required;

            ApplicationDataContainer localSettings = ApplicationData.Current.LocalSettings;
            if (!localSettings.Values.ContainsKey("defaultTables"))
            {
                Other.Other.objConn.Prepare(@"CREATE TABLE jsons (id integer primary key autoincrement, what varchar(255), json mediumtext);").Step();
                Other.Other.objConn.Prepare(@"CREATE TABLE favorites (id integer primary key autoincrement, title text, description text, post mediumtext, image text, url text, comments varchar(11), date varchar(20), author varchar(50));").Step();
                Other.Other.objConn.Prepare(@"INSERT INTO jsons (what, json) VALUES ('menu', '');").Step();
                Other.Other.objConn.Prepare(@"INSERT INTO jsons (what, json) VALUES ('main', '');").Step();

                localSettings.Values["defaultTables"] = true;
            }

            tmr.Interval = TimeSpan.FromMilliseconds(0);
            tmr.Tick += GoFocus;

            post = PostFrame;
            posts = PostsFrame;
            MainPage.header.SelectedIndex = 0;
        }

        private void CBSearch_Click(object sender, RoutedEventArgs e)
        {
            SearchBox.Visibility = Visibility.Visible;
            CBTitle.Visibility = Visibility.Collapsed;
            CBSearch.Visibility = Visibility.Collapsed;
            tmr.Start();
        }

        private void SearchBox_LostFocus(object sender, RoutedEventArgs e)
        {
            SearchBox.Visibility = Visibility.Collapsed;
            CBTitle.Visibility = Visibility.Visible;
            CBSearch.Visibility = Visibility.Visible;
        }

        public void GoFocus(object sender, object e)
        {
            SearchBox.Focus(FocusState.Programmatic);
            tmr.Stop();
        }

        public void Refresh_Click(object sender, RoutedEventArgs e)
        {
            Posts.LoadPosts(false);
        }

        private void OpenSite_Click(object sender, RoutedEventArgs e)
        {
            OpenBrowser();
        }

        public async void OpenBrowser()
        {
            await Launcher.LaunchUriAsync(new Uri("http://" + Other.Other.loader.GetString("SiteUrl")));
        }

        private void SearchBox_QuerySubmitted(AutoSuggestBox sender, AutoSuggestBoxQuerySubmittedEventArgs args)
        {
            string[] info = { "Busca: " + args.QueryText, "http://www.zelda.com.br/search/node/" + args.QueryText };

            if (MainPage.toWebView)
            {
                WebViewPage.title.Text = info[0];
                WebViewPage.WV.Navigate(new Uri(info[1]));
            }
            else
            {
                MainPage.MFrame.Navigate(typeof(WebViewPage), info);
            }

            MainPage.toWebView = true;

            SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Visible;

            MainPage.header.SelectedIndex = -1;
        }
    }
}
