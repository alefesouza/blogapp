using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Net.Http;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Data.Json;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Core;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Media.Imaging;
using Windows.UI.Xaml.Navigation;
using DropandoIdeias.Models;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace DropandoIdeias
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class Posts : Page
    {
        public static Image hImage;
        public static ListView lPosts;
        public static ProgressBar pBar;
        public static ProgressRing pRing, toKnow;

        public Posts()
        {
            this.InitializeComponent();
            this.NavigationCacheMode = NavigationCacheMode.Required;
            hImage = HeaderImage;
            lPosts = ListPosts;
            pBar = PBar;
            pRing = PRing;
            toKnow = ToKnow;
            LoadPosts(true);
            if (Other.Other.IsConnected())
            {
                LoadPosts(false);
            }

            if (HomePage.post.Visibility == Visibility.Collapsed)
            {
                SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Collapsed;
            }

            SystemNavigationManager.GetForCurrentView().BackRequested += OnBackRequested;
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            if (Other.Other.getJson("main").Equals(""))
            {
                LoadPosts(false);
            }
        }

        public static void OnBackRequested(object sender, BackRequestedEventArgs e)
        {
            if (MainPage.toSettings)
            {
                SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = HomePage.post.Visibility == Visibility.Visible ? AppViewBackButtonVisibility.Visible : AppViewBackButtonVisibility.Collapsed;
                MainPage.toSettings = false;

                MainPage.set.SelectedIndex = -1;
                MainPage.header.SelectedIndex = MainPage.selectedIndex;
                MainPage.MFrame.GoBack();
            }
            else if (MainPage.toWebView)
            {
                if (WebViewPage.WV.CanGoBack)
                {
                    WebViewPage.WV.GoBack();
                }
                else
                {
                    SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = HomePage.post.Visibility == Visibility.Visible ? AppViewBackButtonVisibility.Visible : AppViewBackButtonVisibility.Collapsed;
                    MainPage.toWebView = false;

                    MainPage.MFrame.GoBack();

                    MainPage.header.SelectedIndex = MainPage.selectedIndex;
                }
            }
            else
            {
                if (HomePage.post.Visibility == Visibility.Visible)
                {
                    if (HomePage.post.CanGoBack)
                    {
                        HomePage.post.GoBack();
                    }
                    else
                    {
                        HomePage.post.Visibility = Visibility.Collapsed;
                        SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Collapsed;
                    }
                }
                else
                {
                    Application.Current.Exit();
                }
            }
            e.Handled = true;
        }

        public static async void LoadPosts(bool isOff)
        {
            pBar.Visibility = Visibility.Visible;

            ObservableCollection<PostItem> Items = new ObservableCollection<PostItem>();
            
            JsonArray root;
            string header;
            if (isOff && !Other.Other.getJson("main").Equals(""))
            {
                root = JsonValue.Parse(Other.Other.getJson("main")).GetObject().GetNamedArray("posts");
                header = JsonValue.Parse(Other.Other.getJson("main")).GetObject().GetNamedString("header");
            }
            else
            {
                string jsonString = "";

                try
                {
                    jsonString = await new HttpClient().GetStringAsync(new Uri(Other.Other.defaultUrl + "json.php?platform=windows"));
                }
                catch (Exception e)
                {
                    if(Other.Other.getJson("main").Equals(""))
                    {
                        HomePage.posts.Navigate(typeof(ErrorPage));
                    }
                    else
                    {
                        Other.Other.ShowMessage("Verifique sua conexão de internet");
                        pBar.Visibility = Visibility.Collapsed;
                    }
                    return;
                }

                if (!jsonString.Equals(Other.Other.getJson("main")))
                {
                    Other.Other.objConn.Prepare("UPDATE jsons SET json='" + jsonString + "' WHERE what='main';").Step();
                }
                else
                {
                    if (HomePage.post.Visibility == Visibility.Collapsed && toKnow.IsActive)
                    {
                        lPosts.SelectedIndex = 0;
                    }
                    pBar.Visibility = Visibility.Collapsed;
                    return;
                }
                root = JsonValue.Parse(jsonString).GetObject().GetNamedArray("posts");
                header = JsonValue.Parse(jsonString).GetObject().GetNamedString("header");
            }

            hImage.Source = new BitmapImage(new Uri(header, UriKind.Absolute));

            for (uint i = 0; i < root.Count; i++)
            {
                string title = root.GetObjectAt(i).GetNamedString("title").Replace("(apos)", "'");
                string description = root.GetObjectAt(i).GetNamedString("description").Replace("(apos)", "'");
                string post = root.GetObjectAt(i).GetNamedString("post");
                string image = root.GetObjectAt(i).GetNamedString("image");
                string url = root.GetObjectAt(i).GetNamedString("url");
                string comments = root.GetObjectAt(i).GetNamedString("comments");
                string date = root.GetObjectAt(i).GetNamedString("date");
                string author = root.GetObjectAt(i).GetNamedString("author");

                PostItem item = new PostItem(title, description, post, image, url, comments, date, author);
                Items.Add(item);

                if(i == 0 && HomePage.post.Visibility == Visibility.Collapsed && toKnow.IsActive)
                {
                    HomePage.post.Navigate(typeof(Post), item);

                    SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Visible;
                    HomePage.post.Visibility = Visibility.Visible;
                }
            }

            Binding myBinding = new Binding();
            myBinding.Source = Items;
            lPosts.SetBinding(ItemsControl.ItemsSourceProperty, myBinding);

            pBar.Visibility = Visibility.Collapsed;
            pRing.Visibility = Visibility.Collapsed;
            lPosts.Visibility = Visibility.Visible;
        }

        public void ListPosts_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (ListPosts.SelectedIndex != -1)
            {
                if(HomePage.post.Visibility == Visibility.Collapsed)
                {
                    HomePage.post.SetNavigationState("1,0");
                }

                PostItem a = e.AddedItems[0] as PostItem;
                HomePage.post.Navigate(typeof(Post), a);

                SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Visible;
                HomePage.post.Visibility = Visibility.Visible;

                ListPosts.SelectedIndex = -1;
            }
        }
    }
}