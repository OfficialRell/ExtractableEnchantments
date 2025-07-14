package mc.rellox.extractableenchantments.api.extractor.extract;

public interface IExtract {
	
	boolean unsafe();
	
	ExtractType type();
	
	ExtractFilter filter();
	
	IAccepted accepted();

}
