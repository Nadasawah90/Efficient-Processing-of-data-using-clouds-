/**
 * Copyright 2012 Jee Vang 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0 
 *  
 *  Unless required by applicable law or agreed to in writing, software 
 *  distributed under the License is distributed on an "AS IS" BASIS, 
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 *  See the License for the specific language governing permissions and 
 *  limitations under the License. 
 */
package corr.job;

import java.util.Date;
import java.util.Formatter;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.map.MultithreadedMapper;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

import corr.mapreduce.PearsonReducer;
import corr.mapreduce.VariablePairMapper;
import corr.writable.PearsonComputationWritable;
import corr.writable.VariableIndexPairsWritable;
import corr.writable.VariableValuePairsWritable;

/**
 * Job to compute Pearson correlation against CSV text file input.
 * <pre>
 * hadoop jar demo-0.1.jar corr.job.PearsonJob /path/to/toydata10000.csv
 * </pre>
 * @author Jee Vang
 *
 */
public class PearsonJob extends Configured implements Tool {

	/**
	 * Main method.
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {
		int result = ToolRunner.run(new Configuration(), new PearsonJob(), args);
		System.exit(result);
	}

	@Override
	public int run(String[] args) throws Exception {
		if(args.length != 1) {
			System.err.println("usage: PearsonJob <input path>");
			ToolRunner.printGenericCommandUsage(System.err);
			return -1;
		}
		
		String inPath = args[0];
		Formatter formatter = new Formatter();
        String outPath = "Out"
                + formatter.format("%1$tm%1$td%1$tH%1$tM%1$tS", new Date());
		
        Configuration config = getConf();
//        config.setInt("mapred.line.input.format.linespermap", 100);
		Job job = new Job(config, "pairwise pearson correlation");
		job.setJarByClass(PearsonJob.class); 
//		job.setNumReduceTasks(4);
		
		FileInputFormat.addInputPath(job, new Path(inPath));
		FileOutputFormat.setOutputPath(job, new Path(outPath));

		MultithreadedMapper.setMapperClass(job, VariablePairMapper.class);
		MultithreadedMapper.setNumberOfThreads(job, 8);

		job.setMapperClass(MultithreadedMapper.class);

		job.setReducerClass(PearsonReducer.class);
		
		job.setMapOutputKeyClass(VariableIndexPairsWritable.class);
		job.setMapOutputValueClass(VariableValuePairsWritable.class);
		
		job.setOutputKeyClass(VariableIndexPairsWritable.class);
		job.setOutputValueClass(PearsonComputationWritable.class);
		
		return job.waitForCompletion(true) ? 0 : 1;
	}

}
