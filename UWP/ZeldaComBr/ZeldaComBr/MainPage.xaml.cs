using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Runtime.InteropServices.WindowsRuntime;
using System.Threading.Tasks;
using Windows.ApplicationModel.DataTransfer;
using Windows.Data.Xml.Dom;
using Windows.Foundation;
using Windows.Foundation.Collections;
using Windows.Storage;
using Windows.System;
using Windows.UI.Core;
using Windows.UI.Notifications;
using Windows.UI.Popups;
using Windows.UI.Xaml;
using Windows.UI.Xaml.Controls;
using Windows.UI.Xaml.Controls.Primitives;
using Windows.UI.Xaml.Data;
using Windows.UI.Xaml.Input;
using Windows.UI.Xaml.Media;
using Windows.UI.Xaml.Navigation;

namespace ZeldaComBr
{
    public sealed partial class MainPage : Page
    {
        public static ListBox set, header;
        public static Frame MFrame;
        public static bool toSettings = false, toWebView = false;
        public static int selectedIndex = 0;

        public MainPage()
        {
            this.InitializeComponent();
            this.DataContextChanged += (s, e) => { LeftViewModel = DataContext as ViewModels.LeftPanelViewModel; };

            MFrame = MainFrame;

            ApplicationDataContainer localSettings = ApplicationData.Current.LocalSettings;
            if (!localSettings.Values.ContainsKey("NotifNotifs"))
            {
                var template = $@"
                <toast activationType='background' duration='long'>
                    <visual>
                        <binding template='ToastGeneric'>
                            <text>Notificações - Zelda.com.br</text>
                            <text>Deseja receber notificações de novos posts do site?</text>
                        </binding>
                    </visual>
                    <actions>
                        <action
                            content='Sim'
                            activationType='foreground'
                            arguments='enablenotif'/>

                        <action
                            content='Não'
                            activationType='foreground'
                            arguments='disablenotif'/>
                    </actions>
                </toast>";

                var xml = new XmlDocument();
                xml.LoadXml(template);
                ToastNotification toast = new ToastNotification(xml);
                ToastNotificationManager.CreateToastNotifier().Show(toast);
                
                localSettings.Values["NotifEnabled"] = true;
                localSettings.Values["NotifNotifs"] = true;
            }

            set = SettingsLB;
            header = HeaderLB;

            MainFrame.Navigate(typeof(HomePage));
        }

        private void ListLeftPanel_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (ListLeftPanel.SelectedIndex != -1)
            {
                Models.LeftPanelItem a = e.AddedItems[0] as Models.LeftPanelItem;

                string targetUri;

                if (a.Name.Equals("home"))
                {
                    targetUri = "http://zelda.com.br/";
                }
                else if (a.Name.Equals("facebook"))
                {
                    targetUri = "http://facebook.com/hyrulelegends";
                }
                else if (a.Name.Equals("twitter"))
                {
                    targetUri = "http://twitter.com/hyrulelegends";
                }
                else if (a.Name.Equals("youtube"))
                {
                    targetUri = "http://youtube.com/user/hyrulelegends";
                }
                else
                {
                    targetUri = "http://zelda.com.br/" + a.Name;
                }

                if (toSettings)
                {
                    MainFrame.GoBack();
                    toSettings = false;
                }

                string[] info = { a.Title, targetUri };

                if (toWebView)
                {
                    WebViewPage.title.Text = info[0];
                    WebViewPage.WV.Navigate(new Uri(info[1]));
                }
                else
                {
                    MainFrame.Navigate(typeof(WebViewPage), info);
                }

                toWebView = true;
                toSettings = false;

                SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = AppViewBackButtonVisibility.Visible;

                if (MainSplitView.IsPaneOpen == true)
                {
                    MainSplitView.IsPaneOpen = false;
                }
                ListLeftPanel.SelectedIndex = -1;
                SettingsLB.SelectedIndex = -1;
                HeaderLB.SelectedIndex = -1;
            }
        }

        public ViewModels.LeftPanelViewModel LeftViewModel { get; set; }

        private void HamburguerButton_Click(object sender, RoutedEventArgs e)
        {
            MainSplitView.IsPaneOpen = !MainSplitView.IsPaneOpen;
        }

        public async void showMessage(string Message)
        {
            MessageDialog dialog = new MessageDialog(Message);
            await dialog.ShowAsync();
        }

        private void HeaderLB_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (HeaderLB.SelectedIndex != -1)
            {
                if (toSettings || toWebView)
                {
                    SystemNavigationManager.GetForCurrentView().AppViewBackButtonVisibility = HomePage.post.Visibility == Visibility.Visible ? AppViewBackButtonVisibility.Visible : AppViewBackButtonVisibility.Collapsed;
                    MainFrame.GoBack();
                    toSettings = false;
                    toWebView = false;
                }

                if (HeaderLB.SelectedIndex == 0)
                {
                    HomePage.posts.Navigate(typeof(Posts));
                }
                else
                {
                    HomePage.posts.Navigate(typeof(Favorites));
                }

                if (MainSplitView.IsPaneOpen == true)
                {
                    MainSplitView.IsPaneOpen = false;
                }

                selectedIndex = HeaderLB.SelectedIndex;

                SettingsLB.SelectedIndex = -1;
            }
        }

        private void SettingsLB_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (SettingsLB.SelectedIndex != -1)
            {
                if (toWebView)
                {
                    MainFrame.GoBack();
                    toWebView = false;
                }

                MainFrame.Navigate(typeof(SettingsPage));
                if (MainSplitView.IsPaneOpen)
                {
                    MainSplitView.IsPaneOpen = false;
                }
                HeaderLB.SelectedIndex = -1;
                toSettings = true;
            }
        }

        private void HamburgerLB_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (HamburgerLB.SelectedIndex != -1)
            {
                MainSplitView.IsPaneOpen = !MainSplitView.IsPaneOpen;
                HamburgerLB.SelectedIndex = -1;
            }
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            Other.Other.CreateTile();
        }
    }
}
