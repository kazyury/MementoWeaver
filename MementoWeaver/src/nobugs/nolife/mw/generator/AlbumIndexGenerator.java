package nobugs.nolife.mw.generator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.persistence.TypedQuery;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PathUtil;


public class AlbumIndexGenerator extends SubGenerator {

	@Override
	protected void generateSubMemento() throws MWException {
		String indexfilePath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_ALBUM)+"\\index.html";
		String listFilePath = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_ALBUM)+"\\albums.list";

		// アルバムメメントの対象年、年月
		TreeSet<String> yyyys = new TreeSet<>();
		TreeSet<String> yyyymms = new TreeSet<>();
		List<TaggedMaterial> taggedMaterialList = queryAllMementos().getResultList();
		for(TaggedMaterial tm:taggedMaterialList) {
			Material m = tm.getMaterial();
			yyyys.add(MaterialUtil.getMaterialYear(m));
			yyyymms.add(MaterialUtil.getMaterialYearMonth(m));
		}

		// アルバム目次の作成
		generateIndexPage(indexfilePath, yyyys.descendingSet(), yyyymms);

		// アルバムリストの作成
		generateAlbumList(listFilePath, yyyymms.descendingSet());

	}

	/**
	 * アルバム目次を作成する
	 * @param indexfilePath
	 * @throws MWException
	 */
	private void generateIndexPage(String indexfilePath, Set<String> yyyys, Set<String> yyyymms) throws MWException {

		// velocity用のマップ
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("yyyys",yyyys);
		map.put("yyyymms",yyyymms);

		TemplateWrapper tw = new TemplateWrapper();
		tw.setContext(map);
		tw.setTemplate("nobugs/nolife/mw/generator/template/albumIndex.html");
		tw.setLevel(1);
		tw.setOutput(indexfilePath);
		tw.out();
	}

	/**
	 * アルバムリストを作成する
	 * @param listFilePath
	 * @param yyyymms
	 * @throws MWException
	 */
	private void generateAlbumList(String listFilePath, Set<String> yyyymms) throws MWException {
		File listFile = new File(listFilePath);
		FileWriter writer;
		try {
			writer = new FileWriter(listFile);
			for(String yyyymm:yyyymms) {
				writer.write("a_"+yyyymm+".html\r\n");
			}
			writer.close();
		} catch (IOException e) {
			throw new MWException("アルバムリスト作成時にIOExceptionが発生しました",e);
		}
	}

	/**
	 * Albumタグが付与されたNOT_IN_USE以外の全てのTaggedMaterialを取得するためのクエリを返却する
	 * @return Albumタグが付与されたNOT_IN_USE以外の全てのTaggedMaterialを取得するためのTypedQuery
	 */
	private TypedQuery<TaggedMaterial> queryAllMementos() {
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE tm.id.tag = 'album' AND tm.tagState <> :tagState",TaggedMaterial.class);
		query.setParameter("tagState", Constants.TAG_STATE_NOT_IN_USE);
		return query;
	}

}
