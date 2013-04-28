package nobugs.nolife.mw.dao;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import nobugs.nolife.mw.entities.TagConfig;
import nobugs.nolife.mw.util.PersistenceUtil;

public class TagConfigDao {
	private static EntityManager em = PersistenceUtil.getMWEntityManager();

	/**
	 * 事前定義済みのTag一覧を返却する.
	 * @return 事前定義済みタグ(String)のリスト
	 */
	public List<String> findTagList() {
		TypedQuery<String> query = em.createQuery(
				"SELECT tagConfig.tag FROM TagConfig tagConfig",String.class);
		return query.getResultList();
	}
	
	/**
	 * システムで永続化管理されている全てのタグ設定を取得する.
	 * @return システムで永続化管理されているタグ設定のリスト
	 */
	public List<TagConfig> findAll() {
		TypedQuery<TagConfig> query = em.createQuery(
				"SELECT tagConfig FROM TagConfig tagConfig",TagConfig.class);
		return query.getResultList();
	}

	/**
	 * 指定されたTagのタグ設定を取得する.
	 * @return システムで永続化管理されているタグ設定
	 */
	public TagConfig find(String tag) {
		return em.find(TagConfig.class, tag);
	}

	/**
	 * 指定されたジェネレータ名に対応するサブジェネレータFQCNを返却する.
	 * @param generatorName
	 * @return 指定されたジェネレータのサブジェネレータFQCN. 該当なしの場合はNullを返却
	 */
	public String findSubGeneratorFQCNByGeneratorName(String generatorName) {
		TypedQuery<String> query = em.createQuery(
				"SELECT tagConfig.subGeneratorFqcn FROM TagConfig tagConfig WHERE tagConfig.generatorName = :generatorName",String.class);
		query.setParameter("generatorName", generatorName);
		List<String> resultList = query.getResultList();
		if(resultList.isEmpty() || resultList.size()==0) {
			return null;
		} else {
			return resultList.get(0);
		}
	}

	/**
	 * 指定されたカテゴリに対応する主ジェネレータ名を返却する.
	 * @param category
	 * @return 指定されたカテゴリのジェネレータ名. 該当なしの場合はNullを返却
	 */
	public String findGeneratorNameByCategory(String category) {
		TypedQuery<String> query = em.createQuery(
				"SELECT tagConfig.generatorName FROM TagConfig tagConfig WHERE tagConfig.category = :category",String.class);
		query.setParameter("category", category);
		List<String> resultList = query.getResultList();
		if(resultList.isEmpty() || resultList.size()==0) {
			return null;
		} else {
			return resultList.get(0);
		}
	}

}
