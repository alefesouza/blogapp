using SQLitePCL;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.UI.Core;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;
using DropandoIdeias.Models;

// The Blank Page item template is documented at http://go.microsoft.com/fwlink/?LinkId=234238

namespace DropandoIdeias
{
    /// <summary>
    /// An empty page that can be used on its own or navigated to within a Frame.
    /// </summary>
    public sealed partial class Favorites : Page
    {
        public static ListView lPosts;
        public static Grid W;
        public static bool isLoaded = false;

        public Favorites()
        {
            this.InitializeComponent();
            this.NavigationCacheMode = NavigationCacheMode.Required;
            lPosts = ListPosts;
            W = Warning;

            isLoaded = true;
            LoadPosts();

            if (HomePage.post.Visibility == Visibility.Collapsed)
            {
                SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Collapsed;
            }
        }

        public static void LoadPosts()
        {
            if (isLoaded)
            {
                ObservableCollection<PostItem> Items = new ObservableCollection<PostItem>();

                string QuestionPhrase = @"SELECT * FROM favorites";
                var posts = Other.Other.objConn.Prepare(QuestionPhrase);

                while (posts.Step() == SQLiteResult.ROW)
                {
                    Items.Add(new PostItem(posts[1].ToString(), posts[2].ToString(), posts[3].ToString(), posts[4].ToString(), posts[5].ToString(), posts[6].ToString().Split(' ')[0], posts[7].ToString(), posts[8].ToString()));
                }

                W.Visibility = Items.Count > 0 ? Visibility.Collapsed : Visibility.Visible;

                Binding myBinding = new Binding();
                myBinding.Source = Items.Reverse();
                lPosts.SetBinding(ItemsControl.ItemsSourceProperty, myBinding);

                lPosts.Visibility = Visibility.Visible;
            }
        }

        public void ListPosts_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (ListPosts.SelectedIndex != -1)
            {
                PostItem a = e.AddedItems[0] as PostItem;
                HomePage.post.Navigate(typeof(Post), a);

                SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Visible;
                HomePage.post.Visibility = Visibility.Visible;

                ListPosts.SelectedIndex = -1;
            }
        }
    }
}
