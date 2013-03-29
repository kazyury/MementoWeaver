package nobugs.nolife.mw.generator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.TypedQuery;

import nobugs.nolife.mw.MWException;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.util.AgeCalculator;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.MaterialUtil;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.StringUtil;

public class ChronicleGenerator extends Generator {

	@Override
	protected String affectedMemento(Material m, String tag) {
		return getMementoId(m,tag)+".xml";
	}

	/** TaggedMaterial tm�Ɠ��ꃁ�����g�ɑ�����TaggedMaterial���������邽�߂�TypedQuery��ԋp���� */
	@Override
	protected TypedQuery<TaggedMaterial> queryBelongingSameMementoWith(TaggedMaterial tm) {
		TypedQuery<TaggedMaterial> query = em.createQuery(
				"SELECT tm FROM Material m , m.taggedMaterials tm " +
						"WHERE tm.id.tag = :tagname AND tm.tagState <> :tagState",TaggedMaterial.class);
		query.setParameter("tagname", tm.getId().getTag());
		query.setParameter("tagState", Constants.TAG_STATE_NOT_IN_USE);
		return query;
	}

	@Override
	protected Memento generateMemento(List<TaggedMaterial> updateTargetList) throws MWException {

		// �擪1����TaggedMaterial
		String tag = updateTargetList.get(0).getId().getTag();
		Material m = updateTargetList.get(0).getMaterial();
		String outfile = PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_CHRONICLE)+"\\"+affectedMemento(m,tag);

		// velocity�p�̃}�b�v
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("g",this);
		map.put("name",tag); // name = tagname
		map.put("list",updateTargetList);

		TemplateWrapper tw = new TemplateWrapper();
		tw.setContext(map);
		tw.setTemplate("nobugs/nolife/mw/generator/template/chronicle.xml");
		tw.setLevel(1);
		tw.setOutput(outfile);
		tw.out();

		// �ԋp�p�������g�𐶐�
		Memento memento = new Memento();
		memento.setMementoId(tag);
		memento.setCategory("chronicle");
		memento.setProductionPath(outfile);
		// mapped by mementos(TaggedMaterial)�Ȃ̂ŁATaggedMaterial����mementos��ݒ�
		for(TaggedMaterial tm:updateTargetList) {
			tm.getMementos().add(memento);
		}
		memento.setTaggedMaterials(updateTargetList);

		return memento;
	}

	@Override
	protected String getMementoId(Material m, String tag) {
		return tag; // chronicle�̏ꍇ�ɂ�tag�����������gID
	}

	/**
	 * �e���v���[�g����̌Ăяo���p
	 * @see AgeCalculator#calcAgeAsFormattedString(String, String)
	 * @param tm
	 * @return
	 */
	public String calcAge(TaggedMaterial tm) {
		String key = tm.getId().getTag();
		String yyyymmdd = MaterialUtil.getMaterialYearMonthDate(tm.getMaterial());
		return AgeCalculator.calcAgeAsFormattedString(key, yyyymmdd);
	}

	/**
	 * �e���v���[�g����̌Ăяo���p.
	 * @see StringUtil#htmlEncode(String)
	 * @param memo
	 * @return
	 */
	public String htmlEncode(String memo){
		return StringUtil.htmlEncode(memo);
	}

	/**
	 * �e���v���[�g����̌Ăяo���p
	 * @see StringUtil#formatChronicleDate(String, String, String, String, String, String)
	 * @param tm
	 * @return
	 * @throws MWException
	 */
	public String formatChronicleDate(TaggedMaterial tm) throws MWException{
		Material m = tm.getMaterial();
		String year = MaterialUtil.getMaterialYear(m);
		String month = MaterialUtil.getMaterialMonth(m);
		String date = MaterialUtil.getMaterialDate(m);
		String hour = MaterialUtil.getMaterialHour(m);
		String minute = MaterialUtil.getMaterialMinute(m);
		String second = MaterialUtil.getMaterialSecond(m);
		return StringUtil.formatChronicleDate(year, month, date, hour, minute, second);
	}
}
