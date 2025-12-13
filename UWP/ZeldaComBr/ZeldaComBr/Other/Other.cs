using Parse;
using SQLitePCL;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Windows.ApplicationModel.Resources;
using Windows.Networking.Connectivity;
using Windows.UI.Notifications;
using Windows.UI.Popups;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Media.Animation;

namespace ZeldaComBr.Other
{
    public static class Other
    {
        public static string defaultUrl = "http://apps.aloogle.net/blogapp/zeldacombr/";
        public static SQLiteConnection objConn = new SQLiteConnection("ZeldaComBr.db");
        public static ResourceLoader loader = new ResourceLoader();

        public static bool JsonExists(string name)
        {
            string sql = @"SELECT * FROM jsons WHERE what='" + name + "';";
            var jsonsql = objConn.Prepare(sql);
            int i = 0;
            while (jsonsql.Step() == SQLiteResult.ROW)
            {
                i++;
            }
            return i > 0;
        }

        public static string getJson(string name)
        {
            string sql = @"SELECT * FROM jsons WHERE what='" + name + "';";
            var jsonsql = objConn.Prepare(sql);
            jsonsql.Step();

            return jsonsql[2].ToString();
        }

        public static string getQuery(string url, string what)
        {
            string[] parts = url.Split(new char[] { '?', '&' });
            foreach(string p in parts) {
                if(p.Contains(what))
                {
                    return p.Split('=')[1];
                }
            }

            return null;
        }

        public static async void ShowMessage(string message)
        {
            MessageDialog mb = new MessageDialog(message);
            await mb.ShowAsync();
        }

        public static bool IsConnected()
        {
            ConnectionProfile connections = NetworkInformation.GetInternetConnectionProfile();
            bool connected = connections != null && connections.GetNetworkConnectivityLevel() == NetworkConnectivityLevel.InternetAccess;
            return connected;
        }

        public static async void Notif(bool enable)
        {
            if(IsConnected())
            {
                if (enable)
                {
                    await ParsePush.SubscribeAsync("");
                }
                else
                {
                    await ParsePush.UnsubscribeAsync("");
                }
            }
        }

        public static void CreateTile()
        {
            if (IsConnected())
            {
                var uris = new List<Uri>
                    {
                        new Uri(defaultUrl + "notifications/notification1.php"),
                        new Uri(defaultUrl + "notifications/notification2.php"),
                        new Uri(defaultUrl + "notifications/notification3.php"),
                        new Uri(defaultUrl + "notifications/notification4.php"),
                        new Uri(defaultUrl + "notifications/notification5.php")
                    };

                TileUpdater LiveTileUpdater = TileUpdateManager.CreateTileUpdaterForApplication();
                LiveTileUpdater.Clear();
                LiveTileUpdater.EnableNotificationQueue(true);
                LiveTileUpdater.StartPeriodicUpdateBatch(uris, PeriodicUpdateRecurrence.Hour);
            }
        }

        public static void Transition(Page p)
        {
            TransitionCollection collection = new TransitionCollection();
            NavigationThemeTransition theme = new NavigationThemeTransition();

            var info = new DrillInNavigationTransitionInfo();

            theme.DefaultNavigationTransitionInfo = info;
            collection.Add(theme);
            p.Transitions = collection;
        }
    }
}
