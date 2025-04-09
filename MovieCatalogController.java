package com.lak.mit.io.moviecatalogservice;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@RestController
@Configuration
@EnableSwagger2
@RequestMapping("/Catalog")
public class MovieCatalogController {

	@Autowired
	private RestTemplate rt ;
	
	
	@GetMapping(path="/hello")
	public String hello() {
		return "Hello world";
	}
	
	@GetMapping(path="/{userId}")
	@HystrixCommand(fallbackMethod = "getCatalog_Fallback")
	public List<CatalogItem> getCatalog(@PathVariable String userId){
		
		
		@SuppressWarnings("unused")
		List<Rating> ratingList = Arrays.asList(new Rating("1234","5"),new Rating("5678","6"));
		
		UserRating ratings = rt.getForObject("http://MOVIE-RATING-SERVICE/rating/user/"+userId,
				UserRating.class);
		

		 return ratings.getRatings().stream().map(rating -> {
			Movie movie = rt.getForObject("http://MOVIE-INFO-SERVICE/movies/"+rating.getmovieId(), Movie.class);
			return new CatalogItem(movie.getName(), "movies", rating.getRating());
			}).
				collect(Collectors.toList());
		
	}
	
	@SuppressWarnings("unused")
	public List<CatalogItem> getCatalog_Fallback(@PathVariable String userId){
		List<CatalogItem> CatalogItem = new ArrayList<CatalogItem>();
		CatalogItem c = new CatalogItem("hystrix ", "fallback ", "method called");
		CatalogItem.add(c);
		return CatalogItem;
		
	}
	
}
