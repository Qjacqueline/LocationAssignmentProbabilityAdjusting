package algorithm;

public class Chromosome{
	// 染色体属性之基因
	private int[][] gene;
	// 染色体属性之适应度
	private double fitness;


	/**
	 * 染色体构造方法，在初始化一个染色体实例的同时计算其适应度。
	 * 
	 * @param gene
	 */
	public Chromosome(int[][] gene)
	{
		this.gene = gene;
		this.fitness = Fitness.averageRehandling(gene);
	}

	
	public Chromosome() {

	}

	@Override
	public Chromosome clone() {
		Chromosome clonedCh = new Chromosome();
		clonedCh.fitness = this.fitness;
		clonedCh.gene = ArrayUtils.twoDimensionArrayClone(this.gene);
		return clonedCh;
	}
	
	
	public int[][] getGene()
	{		
		return gene;
	}
	
	public void setGene(int[][] gene) {
		this.gene=gene;
		this.fitness = Fitness.averageRehandling(gene);
	}

	public double getFitness()
	{
		return fitness;
	}
	
	/**
	 * 比较一个体是否比另一个体的基因好。
	 * 
	 * @param cs
	 *            另一个体。
	 * @return true，如果这个个体的fitness值比另一个体的小，否则返回false。
	 */
	public boolean betterThan(Chromosome cs)
	{
		if (this.fitness < cs.getFitness())
		{
			return true;
		}
		return false;
	}
		
}