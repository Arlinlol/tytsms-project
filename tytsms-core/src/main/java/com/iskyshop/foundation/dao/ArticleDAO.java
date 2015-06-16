package com.iskyshop.foundation.dao;
import org.springframework.stereotype.Repository;
import com.iskyshop.core.base.GenericDAO;
import com.iskyshop.foundation.domain.Article;
@Repository("articleDAO")
public class ArticleDAO extends GenericDAO<Article> {

}