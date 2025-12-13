using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ZeldaComBr.Models
{
    public class LeftPanelItem
    {
        public string Title { get; set; }
        public string Icon { get; set; }
        public string Name { get; set; }
        public string Image { get; set; }
        public string Visibility { get; set; }
        public string Visibility2 { get; set; }

        public LeftPanelItem(string title, string icon, string name, string image, string visibility, string visibility2) {
            Title = title;
            Icon = icon;
            Name = name;
            Image = image;
            Visibility = visibility;
            Visibility2 = visibility2;
        }
    }
}
