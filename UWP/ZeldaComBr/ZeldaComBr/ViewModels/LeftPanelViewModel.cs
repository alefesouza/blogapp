using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ZeldaComBr.ViewModels
{
    public class LeftPanelViewModel
    {
        public LeftPanelViewModel() {
            string[] titles = { "Site", "Artigos", "Notícias", "Spin-offs", "Podcasts", "Detonados", "Traduções", "Mídia", "Facebook", "Twitter", "YouTube" };
            string[] icons = { "\uE80F", "\uE7C3", "\uE8C8", "\uE7FC", "\uE720", "\uE707", "\uE8C1", "\uE768", "\uE909", "\uE909", "\uE909" };
            string[] names = { "", "artigos", "noticias", "spinoffs", "podcast", "detonados", "traducoes", "midia", "facebook", "twitter", "youtube" };
            for(int i = 0; i < titles.Length; i++)
            {
                var item = new Models.LeftPanelItem(titles[i], icons[i], names[i]);
                this.Items.Add(item);
            }
        }

        public ObservableCollection<Models.LeftPanelItem> Items { get; private set; } = new ObservableCollection<Models.LeftPanelItem>();
    }
}
