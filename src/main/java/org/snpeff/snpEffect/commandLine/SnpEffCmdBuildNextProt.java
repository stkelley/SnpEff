package org.snpeff.snpEffect.commandLine;

import org.snpeff.SnpEff;
import org.snpeff.nextProt.NextProtDb;
import org.snpeff.util.Timer;

/**
 * Parse NetxProt XML file and build a database
 *
 * http://www.nextprot.org/
 *
 * @author pablocingolani
 */
public class SnpEffCmdBuildNextProt extends SnpEff {

	String xmlDirName;
	String trIdFile;

	public SnpEffCmdBuildNextProt() {
		super();
	}

	@Override
	public void parseArgs(String[] args) {
		this.args = args;

		if (args.length <= 0) usage(null);

		for (int i = 0; i < args.length; i++) {
			String arg = args[i];

			// Argument starts with '-'?
			if (isOpt(arg)) {
				switch (arg.toLowerCase()) {
				case "-trids":
					if ((i + 1) < args.length) trIdFile = args[++i];
					else usage("Option '-trids' without transcript id file");
					break;

				default:
					usage("Unknonwn option '" + arg + "'");
				}
			} else if ((genomeVer == null) || genomeVer.isEmpty()) genomeVer = args[i];
			else if ((xmlDirName == null) || xmlDirName.isEmpty()) xmlDirName = args[i];
		}

		// Sanity check
		if ((genomeVer == null) || genomeVer.isEmpty()) usage("Missing genome version");
		if ((xmlDirName == null) || xmlDirName.isEmpty()) usage("Missing nextProt XML dir");
	}

	/**
	 * Run main analysis
	 */
	@Override
	public boolean run() {
		// Initialzie
		loadConfig(); // Read config file
		loadDb();

		NextProtDb nextProtDb = new NextProtDb(xmlDirName, config);
		nextProtDb.setVerbose(verbose);
		nextProtDb.setDebug(debug);
		nextProtDb.setTrIdFile(trIdFile);
		nextProtDb.parse(); // Parse XML files
		nextProtDb.saveDatabase(); // Save database

		if (verbose) Timer.showStdErr("Done!");
		return true;
	}

	@Override
	public void usage(String message) {
		if (message != null) System.err.println("Error        :\t" + message);
		System.err.println("snpEff version " + VERSION);
		System.err.println("Usage: snpEff buildNextProt [options] genome_version nextProt_XML_dir");
		System.err.println("\t-trIds <file.txt>      : Transcript IDs map file. Format 'ENSEMBL_TR_ID \t REFSEQ_TR_ID'.");
		System.exit(-1);
	}

}
