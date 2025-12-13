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

        public LeftPanelItem(string title, string icon, string name) {
            this.Title = title;
            this.Icon = icon;
            this.Name = name;
        }
    }
}
