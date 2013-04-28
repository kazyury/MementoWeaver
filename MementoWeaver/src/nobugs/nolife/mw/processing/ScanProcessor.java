package nobugs.nolife.mw.processing;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import nobugs.nolife.mw.dao.MementoDao;
import nobugs.nolife.mw.dto.ScannedMaterialDTO;
import nobugs.nolife.mw.entities.Material;
import nobugs.nolife.mw.entities.Memento;
import nobugs.nolife.mw.entities.ScannedResult;
import nobugs.nolife.mw.entities.TaggedMaterial;
import nobugs.nolife.mw.exceptions.MWException;
import nobugs.nolife.mw.exceptions.MWResourceIOError;
import nobugs.nolife.mw.util.Constants;
import nobugs.nolife.mw.util.PathUtil;
import nobugs.nolife.mw.util.PersistenceUtil;

public class ScanProcessor {
	private static Logger logger = Logger.getGlobal();
	private static EntityManager em = PersistenceUtil.getMWEntityManager();
	private String scanmode;

	public void setScanMode(String scanmode) {
		this.scanmode = scanmode;
	}
	
	public List<ScannedResult> scanMementoFiles() {
		// TODO *.html�ȊO�ɂ��Ή�������
		// MWROOT�ȉ���*.html��S����
		MementoFinder finder = new MementoFinder("*.html", scanmode);
		File start = new File(PathUtil.getDirectoryProperty(Constants.DIRPROP_KEY_MW_ROOT));
		try {
			Files.walkFileTree(start.toPath(), finder);
		} catch (IOException e) {
			logger.severe("��������IOException���������܂���.");
			throw new MWResourceIOError("��������IOException���������܂���.",e);
		}
		return finder.getResult();
	}


	/**
	 * 
	 * @param scannedResult
	 * @throws MWException 
	 */
	public List<ScannedMaterialDTO> scanMaterialsIn(ScannedResult scannedResult) {
		
		List<ScannedMaterialDTO> materialList = new ArrayList<ScannedMaterialDTO>();
		Memento memento = scannedResult.getMemento();
		
		// ���K�\���ŃX�L���� ../materials/20100102_012345.jpg �Ƃ��B
		String regexp = "\\.\\.\\/materials\\/\\d{8}_\\d{6}\\.(jpg|mov)";
		Pattern p = Pattern.compile(regexp);

		File in = new File(scannedResult.getProductionPath());
		try {
			for(String line:Files.readAllLines(in.toPath(), Charset.forName("MS932"))){
				Matcher matcher = p.matcher(line);
				while(matcher.find()){
					File materialPath = new File(matcher.group());
					Path path = materialPath.toPath();
					String materialId = PathUtil.toMaterialId(path);

					// dto�쐬
					ScannedMaterialDTO dto = new ScannedMaterialDTO();
					dto.setPath(path);
					dto.setMateiralId(materialId);
					
					Material m = em.find(Material.class, materialId);
					if(m != null) {
						logger.fine("�������g["+in.toString()+"]���̑f��["+path.toString()+"]��Material�e�[�u���ɓo�^����Ă��܂�");
						dto.setRegisteredMaterial(true);
					} else {
						logger.fine("�������g["+in.toString()+"]���̑f��["+path.toString()+"]��Material�e�[�u���ɓo�^����Ă��܂���");
						dto.setRegisteredMaterial(false);
					}
					
					// memento����TaggedMaterials�ɓ���MateiralId���܂܂�Ă�����A����DB�o�^�ς�
					boolean belongToMemento = false;
					if(!(memento == null || memento.getTaggedMaterials().isEmpty())){
						for(TaggedMaterial tm:memento.getTaggedMaterials()) {
							if(tm.getId().getMaterialId().equals(materialId)) {
								belongToMemento = true;
							}
						}
					}
					dto.setBelongToMemento(belongToMemento);
					
					// ���ʃZ�b�g�ւ̒ǉ�
					materialList.add(dto);
				}
			}
		} catch (IOException e) {
			throw new MWResourceIOError(e);
		}
		return materialList;
	}

	
	/**
	 * �w�肳�ꂽ�p�^�[���Ƀ}�b�`����t�@�C���𑖍�����.
	 * @see <a href="http://docs.oracle.com/javase/tutorial/essential/io/walk.html">Walking the File Tree</a>
	 * @author kazyury
	 *
	 */
	public static class MementoFinder extends SimpleFileVisitor<Path> {
		private final PathMatcher matcher;
		private static EntityManager em = PersistenceUtil.getMWEntityManager();
		private static Logger logger = Logger.getGlobal();
		private String scanmode;
		private List<ScannedResult> resultList = new ArrayList<ScannedResult>();
		private MementoDao mementoDao = new MementoDao();
		
		public MementoFinder(String pattern, String scanmode) {
			matcher=FileSystems.getDefault().getPathMatcher("glob:"+pattern);
			this.scanmode = scanmode;
			logger.info("MementoFinder���p�^�[��["+pattern+"],�X�L�������[�h["+scanmode+"]�ō쐬����܂���");
		}
		
		public List<ScannedResult> getResult() {
			return resultList;
		}

		// Invoke the pattern matching method on each file.
		// �����File�ɑ΂��鑖�������Ȃ̂�preVisitDirectory����override�s�v
		@Override
		public FileVisitResult visitFile(Path path, BasicFileAttributes bfa) throws IOException {
			Path name = path.getFileName();
			logger.fine("["+name.toString()+"]���}�b�`���邩�`�F�b�N��...");
			if(name != null && matcher.matches(name)){
				logger.fine("Found ["+path.toString()+"]");
				ScannedResult sr = findOrCreateScannedResult(path);
				
				// scanmode�ɂ�鋓���ύX
				if(scanmode.equals(Constants.SCANTYPE_ALL)) {
					resultList.add(sr);
				} else if(scanmode.equals(Constants.SCANTYPE_IGNORED) && sr.getIgnored().equals(Constants.SCAN_IGNORED)) {
					resultList.add(sr);
				} else if (scanmode.equals(Constants.SCANTYPE_NOT_IGNORED) && sr.getIgnored().equals(Constants.SCAN_NOT_IGNORED)) {
					resultList.add(sr);
				} else {
					logger.fine("ScanType["+scanmode+"]:ScannedResult("+sr.getProductionPath()+")��Ignored=["+sr.getIgnored()+"]�̈׌��ʃ��X�g�Ɋ܂܂�܂���.");
				}
			}
			return FileVisitResult.CONTINUE;
		}
		
		private ScannedResult findOrCreateScannedResult(Path path) {
			Memento memento = mementoDao.findByPath(path.toString());
			ScannedResult result = em.find(ScannedResult.class, path.toString());
			if(result != null) {
				logger.fine(path.toString()+" already exist. use existing ScannedResult.");
				if(memento != null) {
					result.setMemento(memento);
				}
				return result;
			} else {
				ScannedResult sr = new ScannedResult();
				sr.setProductionPath(path.toString());
				sr.setIgnored(Constants.SCAN_NOT_IGNORED);
				if(memento != null) {
					sr.setMemento(memento);
				}
				em.getTransaction().begin();
				em.persist(sr);
				em.getTransaction().commit();
				logger.fine(path.toString()+" was not exist. new ScannedResult persisted.");
				return sr;
			}
		}
	}
}
