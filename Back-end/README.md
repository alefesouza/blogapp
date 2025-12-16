## BlogApp Back-end

Back-end for BlogApp apps, latest modification in 2015.

At the time I had 17-18 years old and I was learning PHP by myself for about one year, so don't care about the coding standards used here, because I didn't know about most of PHP functions, like `json_encode` 😂

The apps data are mainly a wrapper to the WordPress and Blogger APIs.

The `blogapp.php` file was something the blog owner had to upload to their website’s root directory so the apps could fetch WordPress data (I didn’t know about the WordPress REST APIs when I created this method, it was not needed on [./wordpress/v2](./wordpress/v2)). You’re allowed to use it and just ignore the second line 😂 I wanted to keep this file untouched, only removing the FB comments access tokens.
