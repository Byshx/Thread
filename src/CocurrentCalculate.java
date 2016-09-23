
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class CocurrentCalculate {
	//通过迭代Future来完成数组相加，如果前一个任务未完成将会造成线程阻塞
	private ExecutorService executorService;
	private int cpuCoreNumber;
	private List<Future<Long>> tasks = new ArrayList<Future<Long>>();

	class SumCalculator implements Callable<Long> {
		private int[] numbers;
		private int start;
		private int end;

		public SumCalculator(final int[] numbers, int start, int end) {
			this.start = start;
			this.end = end;
			this.numbers = numbers;
		}

		public Long call() throws Exception {
			Long sum = 0l;
			for (int i = start; i < end; i++) {
				sum += numbers[i];
			}
			return sum;
		}
	}

	public CocurrentCalculate() {
		// TODO Auto-generated constructor stub
		cpuCoreNumber = Runtime.getRuntime().availableProcessors();
		executorService = Executors.newFixedThreadPool(cpuCoreNumber);
	}

	public Long sum(final int[] numbers) {
		// 根据CPU个数拆分任务，创建FutureTask并提交到Executor
		for (int i = 0; i < cpuCoreNumber; i++) {
			int increment = numbers.length / cpuCoreNumber + 1;
			int start = increment * i;
			int end = increment * i + increment;
			if (end > numbers.length)
				end = numbers.length;
			SumCalculator sumCalc = new SumCalculator(numbers, start, end);
			FutureTask<Long> task = new FutureTask<Long>(sumCalc);
			tasks.add(task);
			if (!executorService.isShutdown()) {
				executorService.submit(task);
			}
		}
		return getResult();
	}

	public Long getResult() {
		Long result = 0l;
		for (Future<Long> task : tasks) {
			Long subSum;
			try {
				subSum = task.get();
				result += subSum;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}

	public void close() {
		executorService.shutdown();
	}

	public long Sum(final int[] numbers) {
		long tmp = 0l;
		for (int i = 0; i < numbers.length; i++) {
			tmp += numbers[i];
		}
		return tmp;
	}

	public static void main(String[] args) {
		final int[] numbers = { 1, 2, 3, 4, 5, 6, 7, 8, 9 };
		CocurrentCalculate calculate = new CocurrentCalculate();
		long a = System.currentTimeMillis();
		Long tmp = calculate.sum(numbers);
		long b = System.currentTimeMillis();
		System.out.println("并发运算结果：" + tmp + " 耗时：" + (b - a) + "ms");
		a = System.currentTimeMillis();
		long temp = calculate.Sum(numbers);
		b = System.currentTimeMillis();
		System.out.println("单线程运算结果：" + temp + " 耗时：" + (b - a) + "ms");
		calculate.close();
	}
}
