import edu.asu.cse.cbioc.intex.IntEx;

class Test{
	public static void main(String[] args) {
		//15933811
		//15708677
		//15900280
		//9566895
		//15900286
		
		int i = 15933218;
		try{
			IntEx ie = IntEx.extractFromPubMed(i);
			
			ie.getBioMedAbstract().saveOriginalToFile(i+".txt");
			ie.getBioMedAbstract().saveWorkingToFile(i+".gene.txt");
			ie.getBioMedAbstract().saveInteractionsToFile(i+".interactions.txt");
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
}