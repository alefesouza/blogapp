<?php
// Developed by Alefe Souza <http://alefesouza.com>, unauthorized use without permission

include('wp-load.php');

$mode = $_GET["mode"];

if($mode == "main") {
  if(isset($_GET["search"])) {
    $posts = new WP_Query(array('post_type' => 'post', 'posts_per_page' => 10, 'paged' => $_GET["page"], 's' => $_GET["search"]));
  } else if(isset($_GET["category"])) {
    $posts = new WP_Query(array('post_type' => 'post', 'posts_per_page' => 10, 'paged' => $_GET["page"], 'category_name' => $_GET["category"]));
  } else if(isset($_GET["tag"])) {
    $posts = new WP_Query(array('post_type' => 'post', 'posts_per_page' => 10, 'paged' => $_GET["page"], 'tag' => $_GET["tag"]));
  } else {
    $posts = new WP_Query(array('post_type' => 'post', 'posts_per_page' => 10, 'paged' => $_GET["page"]));
  }

  $blogapp = array();

  if ($posts->have_posts()) {
            while ($posts->have_posts()) {
              $posts->the_post();

      $id = get_the_ID();
      $title = get_the_title();
      $description = get_post_meta($id, '_yoast_wpseo_metadesc', true); 
      $image = get_post_meta($id, '_yoast_wpseo_twitter-image', true);
      $url = get_the_permalink();
      $comments = get_comments_number();

      $jsonfb = file_get_contents('https://graph.facebook.com/v2.6/?fields=og_object{comments}&id='.$url.'&access_token=');
      $commentsfb = json_decode($jsonfb);
      $comentarios = count($commentsfb->og_object->comments->data);

      $blogapp[] = array("id" => $id, "title" => $title, "description" => $description, "image" => $image, "author" => "", "url" => $url, "comments" => (string)($comments + $comentarios), "categoryicon" => str_replace("valuehere", $categoria, "http://dropandoideias.com/wp-content/cat-icons/valuehere.png"), "date" => "");
    }
  }
} else if($mode == "post") {
  $posts = new WP_Query(array('p' => $_GET["id"]));

  if ($posts->have_posts()) {
            while ($posts->have_posts()) {
              $posts->the_post();

      $id = get_the_ID();
      $title = get_the_title();
      $description = get_post_meta($id, '_yoast_wpseo_metadesc', true); 
      
      if(get_post_meta($id, '_yoast_wpseo_twitter-image', true) != "") {
        $image = get_post_meta($id, '_yoast_wpseo_twitter-image', true);
      } else {
        $image = get_post_meta($id, '_yoast_wpseo_opengraph-image', true);
      }
              
      $url = get_the_permalink();
      $comments = get_comments_number();
      $content = get_the_content();
      $category = get_the_category()[0]->slug;
              
      if(count(get_the_tags()) > 1) {
        foreach (get_the_tags() as $tag) {
          $tags .= '<a href="http://apps.aloogle.net/blogapp/start?tag='.$tag->slug.'&title='.$tag->name.'" class="tag">'.$tag->name.'</a>, ';
        }
      }
              
      if($tags != "") {
        $tags = "<font class=\"tagscall\">Tags:</font> ".$tags;
      }

      $jsonfb = file_get_contents('https://graph.facebook.com/v2.6/?fields=og_object{comments}&id='.$url.'&access_token=');
      $commentsfb = json_decode($jsonfb);
      $comentarios = count($commentsfb->og_object->comments->data);

      $blogapp = array("title" => $title, "description" => $description, "image" => $image, "author" => "", "url" => $url, "comments" => (string)($comments + $comentarios), "categoryicon" => str_replace("valuehere", $category, "http://dropandoideias.com/wp-content/cat-icons/valuehere.png"), "date" => "", "date2" => "", "content" => '<html><head><meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0" /><style>@import url(https://fonts.googleapis.com/css?family=Roboto:400,400italic,500,500italic,700,700italic);

body {
        font-family: \'Roboto\', sans-serif;
	text-align: justify;
	padding: 10px;
	word-wrap: break-word;
}

img, div, iframe {
	max-width: 100% !important;
	height: auto !important;
}

a {
  color:#5AC3D1;
	text-decoration: none;
}

.tagscall {
	font-weight: bold;
}

.tag {
	color: #5AC3D1;
}</style></head><body>'.$content.'<p>'.$tags.'</p></body><script>var imgs = document.getElementsByTagName(\'img\');
for(var i = 0; i < imgs.length; i++) {
	/* código para abrir a tela de zoom ao tocar em uma imagem */
	imgs[i].onclick = function() {
		window.open(\'http://apps.aloogle.net/blogapp/start?image=\' + encodeURIComponent(this.src), \'_blank\');
	};
}

window.onload = function() {
var imgs = document.getElementsByTagName(\'img\');
	for(var i = 0; i < imgs.length; i++) {
		/* se uma imagem for maior que 100 pixeis deixar ela centralizada, esse código funciona melhor após a página carregar  */
		if(imgs[i].clientWidth > 100) {
			imgs[i].style.marginLeft= "auto";
			imgs[i].style.marginRight= "auto";
			imgs[i].style.display= "block";
		}
	}
}</script></html>');
    }
  }
} else if($mode == "tags") {
  $q = $_GET['q'];
  
  $tags = get_tags(array('search' => $q));
  
  $blogapp = array();
  
  foreach($tags as $tag) {
    $blogapp[] = array("category" => $tag->name);
  }
} else if($mode == "categories") {
  if($_GET["page"] < 2) {
    $links = array(
      "name" => "Links",
      "links" => array()
    );

    $links["links"][] = array("name" => "Equipe", "url" => "http://dropandoideias.com/equipe", "icon" => "");
    $links["links"][] = array("name" => "Blog", "url" => "http://dropandoideias.com/blog", "icon" => "");
    $links["links"][] = array("name" => "Parceiros", "url" => "http://dropandoideias.com/parceiros", "icon" => "");
    $links["links"][] = array("name" => "Contato", "url" => "http://dropandoideias.com/contato", "icon" => "");
    $links["links"][] = array("name" => "Anuncie", "url" => "http://dropandoideias.com/anuncie", "icon" => "");
    $links["links"][] = array("name" => "Categorias", "url" => "http://dropandoideias.com/categorias", "icon" => "");
    $links["links"][] = array("name" => "Política", "url" => "http://dropandoideias.com/politica", "icon" => "");

    $links["count"] = count($links["links"]);

    $featured_categories = array(
      "name" => "Categorias principais",
      "featuredcategories" => array()
    );

    $featured_categories["count"] = count($featured_categories["featuredcategories"]);
    
    $page = 1;
  } else {
    $page = $_GET["page"];
  }
  
  $categories = get_categories();
  
  $blogapp = array("total" => count($categories), "categories" => array());
  
  $count = 0;
  
  foreach($categories as $category) {
    if($page > 1) {
      if($count < ($page - 1) * 15) {
        $count++;
        continue;
      }
    }
    
    $blogapp["categories"][] = array("id" => $category->slug, "name" => $category->name, "icon" => str_replace("valuehere", $category->slug, "http://dropandoideias.com/wp-content/cat-icons/valuehere.png"));
    
    $count ++;
    
    if($count == $page * 15) {
      break;
    }
  }
  
  if($_GET["page"] < 2) {
    $blogapp = array("links" => $links, "featuredcategories" => $featured_categories, "categories" => $blogapp);
  } else {
    $blogapp = array("categories" => $blogapp);
  }
} else if($mode == "allcategs") {
  $categories = get_categories();
  
  $blogapp = array("total" => count($categories), "categories" => array());
  
  foreach($categories as $category) {
    $blogapp["categories"][] = array("id" => $category->slug, "name" => $category->name, "icon" => str_replace("valuehere", $category->slug, "http://dropandoideias.com/wp-content/cat-icons/valuehere.png"));
  }
} else if($mode == "random") {
  $posts = new WP_Query(array('post_type' => 'post', 'posts_per_page' => 1, 'orderby' => 'rand'));
  
  if ($posts->have_posts()) {
            while ($posts->have_posts()) {
              $posts->the_post();
  
              $blogapp = array("id" => get_the_ID());
            }
  }
}

echo json_encode($blogapp);
?>
