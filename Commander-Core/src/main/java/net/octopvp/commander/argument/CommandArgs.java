/*
 * Copyright (c) Evan Yu 2024.
 * MIT License
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package net.octopvp.commander.argument;

import lombok.Getter;
import net.octopvp.commander.Commander;

import java.util.*;

@Getter
public class CommandArgs {
    private final Commander commander;

    private final String[] args;

    private final Map<String,Boolean> switches;

    private final Map<String,String> flags;

    private final List<String> argsList;

    private final List<String> preservedArgs;

    private Deque<String> argsDeque;

    public CommandArgs(Commander commander, String[] args, Map<String, Boolean> switches, Map<String, String> flags, List<String> argsList) {
        this.commander = commander;
        this.args = args;
        this.switches = switches;
        this.flags = flags;
        this.argsList = argsList;
        this.preservedArgs = Collections.unmodifiableList(new ArrayList<>(argsList));
    }

    public Deque<String> getArgs() {
        if (argsDeque == null) {
            argsDeque = new ArrayDeque<>(argsList);
        }
        return argsDeque;
    }
}
