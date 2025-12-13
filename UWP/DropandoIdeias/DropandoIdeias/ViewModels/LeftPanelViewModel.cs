using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace DropandoIdeias.ViewModels
{
    public class LeftPanelViewModel
    {
        public LeftPanelViewModel()
        {
            string[] titles = { "Site", "Artigos", "Notícias", "Podcasts", "A série", "Spin-offs", "Detonados", "Traduções", "Mídia", "Facebook", "Twitter", "YouTube" };
            string[] icons = { "\uE80F", "\uE7C3", "\uE8C8", "\uE720", "image", "\uE7FC", "\uE707", "\uE8C1", "\uE768", "\uE909", "\uE909", "\uE909" };
            string[] names = { "", "artigos", "noticias", "podcast", "jogos", "spinoffs", "detonados", "traducoes", "midia", "facebook", "twitter", "youtube" };
            for (int i = 0; i < titles.Length; i++)
            {
                if (names[i].Equals("jogos"))
                {
                    var item = new Models.LeftPanelItem(titles[i], "", names[i], "ms-appx:///Images/Triforce.png", "Collapsed", "Visible");
                    Items.Add(item);
                }
                else
                {
                    var item = new Models.LeftPanelItem(titles[i], icons[i], names[i], "ms-appx:///Images/Triforce.png", "Visible", "Collapsed");
                    Items.Add(item);
                }
            }
        }

        public ObservableCollection<Models.LeftPanelItem> Items { get; private set; } = new ObservableCollection<Models.LeftPanelItem>();
    }
}
