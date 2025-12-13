using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ZeldaComBr.Models
{
    public class PostItem
    {
        public string Title { get; set; }
        public string Description { get; set; }
        public string Post { get; set; }
        public string Image { get; set; }
        public string Url { get; set; }
        public string Comments { get; set; }
        public string Date { get; set; }
        public string Author { get; set; }

        public PostItem(string Title, string Description, string Post, string Image, string Url, string Comments, string Date, string Author)
        {
            this.Title = Title;
            this.Description = Description;
            this.Post = Post;
            this.Image = Image;
            this.Url = Url;
            if (Comments.Equals("0") || Comments.Equals("1"))
            {
                this.Comments = Comments + " COMENTÁRIO";
            }
            else
            {
                this.Comments = Comments + " COMENTÁRIOS";
            }
            this.Date = Date;
            this.Author = Author;
        }
    }
}
