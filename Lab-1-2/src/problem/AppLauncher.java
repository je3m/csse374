package problem;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;

/*
 * Copyright (c) 2008, 2010, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * Modified By:
 * 		Chandan R. Rupakheti
 * --> Note:  See notes for Windows and Mac users!
 */
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This class has been modified from the original WatchDir program found at:
 * https://docs.oracle.com/javase/tutorial/essential/io/examples/WatchDir.java.
 *
 * This program listens to three kind of events in a supplied directory:
 * <ol>
 * 	<li>ENTRY_CREATE - When a file/folder gets created.</li>
 * 	<li>ENTRY_DELETE - When a file/folder gets deleted.</li>
 * 	<li>ENTRY_MODIFY - When a file/folder get modified.</li>
 * </ol>
 *
 * Based on the event it launches the relevant application through the
 * {@link #handleDirectoryEvent(String, Path)} method.
 */
public class AppLauncher extends Thread {
	//TODO: Refactor this class into cohesive classes. Right now, this is an awful God class that does everything.

	public static void main(String[] args) throws IOException, InterruptedException {
		// Register directory to the launcher
		Path dir = Paths.get("./input_output");
		AppLauncher launcher = new AppLauncher(dir);
		launcher.start();

		System.out.format("Launcher started watching %s ...%nPress the return key to stop ...", dir);

		// Wait for an input
		System.in.read();

		launcher.stopGracefully();
		launcher.join();

		System.out.println("Directory watching stopped ...");
	}
	private final WatchService watcher;
	private final Path dir;
	private boolean stop;

	private List<Process> processes;

	/**
	 * Creates a WatchService and registers the given directory
	 */
	AppLauncher(Path dir) throws IOException {
		this.stop = true;
		this.dir = dir;
		this.processes = Collections.synchronizedList(new ArrayList<Process>());
		this.watcher = FileSystems.getDefault().newWatchService();
		dir.register(this.watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
	}

	/**
	 * This method is for internal use to delete the temporary file created by
	 * the {@link #stopGracefully()} method. As well as to kill all of the newly
	 * created process.
	 */
	protected void clearEverything() {
		File file = new File(this.dir.toFile() + "/.temp");
		file.delete();

		for(Process p: this.processes) {
			p.destroy();
		}
	}

	/**
	 * Returns the number of applications launched so far by the launcher.
	 */
	public int getApplicationsCount() {
		return this.processes.size();
	}

	/**
	 * This method gets called when ever the directory being monitored changes.

	 * @param eventName One of the following three strings:
	 * <ol>
	 * 	<li>ENTRY_CREATE - When a file/folder gets created.</li>
	 * 	<li>ENTRY_DELETE - When a file/folder gets deleted.</li>
	 * 	<li>ENTRY_MODIFY - When a file/folder get modified.</li>
	 * </ol>
	 * @param file The file that generate the event
	 * @throws IOException
	 */
	public void handleDirectoryEvent(String eventName, Path file) {

		// We are only interested in the new files that get dropped into the launcher folder
		if(!eventName.equals("ENTRY_CREATE")) {
			return;
		}

		ProcessBuilder processBuilder = null;
		String command = null;
		String arg = null;

		// TODO: F1. Add support for new type of application
		// TODO: F2. Add support for new type of directory event handler
		String fileName = file.toAbsolutePath().toString();
		System.out.println("Processing " + fileName + "...");

		if(fileName.endsWith(".html") || fileName.endsWith(".htm")) {
			// For Windows users, use the next line, instead of the one for Macs.
			command = "explorer";
			// For Mac, instead of "explorer" use the following:
			//			command = "/Applications/Safari.app/Contents/MacOS/Safari";
			//for linux
			//command = "google-chrome-stable";
			arg = fileName;
		}
		else if(fileName.endsWith(".txt")) {
			// For Windows users, use the next line, instead of the one for Macs.
			command = "Notepad";
			// For Mac, instead of "Notepad" use the following:
			//			command = "/Applications/TextEdit.app/Contents/MacOS/TextEdit";
			//for linux
			//command = "subl";
			// The above Mac line has permissions problems -- we're working on that!
			arg = fileName;
		}
		else {
			command = null;
			System.err.format("No support available for: %s...%n", file);
			return;
		}

		// Run the application if support is available
		try {
			System.out.format("Launching %s ...%n", command);
			processBuilder = new ProcessBuilder(command, arg);

			// Start and add the process to the processes list
			Process process = processBuilder.start();
			this.processes.add(process);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns true if the launcher is running, otherwise false.
	 */
	public boolean isRunning() {
		return !this.stop;
	}

	/**
	 * Process all events for keys queued to the watcher
	 */
	@Override
	public void run() {
		this.stop = false;
		while(!this.stop) {
			// Wait for key to be signalled
			WatchKey key;
			try {
				key = this.watcher.take();
			} catch (InterruptedException x) {
				return;
			}

			// Context for directory entry event is the file name of entry
			List<WatchEvent<?>> events = key.pollEvents();
			if(!events.isEmpty()) {
				@SuppressWarnings("unchecked")
				WatchEvent<Path> event = (WatchEvent<Path>)events.get(0);
				Path name = event.context();
				Path child = this.dir.resolve(name);

				// Call the handler method
				this.handleDirectoryEvent(event.kind().name(), child);
			}

			// Reset key and remove from set if directory no longer accessible
			if (!key.reset()) {
				break;
			}
		}

		// We gracefully stopped the service now, let's delete the temp file
		this.clearEverything();
	}

	/**
	 * This method gracefully stops the WatchDir service.
	 * @throws IOException
	 */
	public void stopGracefully() throws IOException {
		this.stop = true;
		File file = new File(this.dir.toFile() + "/.temp");

		// Let's force the while loop in the run method to compe out of the blocking watcher.take() call here
		// You can also create a directory by calling file.mkdir()
		file.createNewFile();
	}
}