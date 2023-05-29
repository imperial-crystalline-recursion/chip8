package com.afewroosloose.chip8driver.ui

import com.afewroosloose.chip8.Display
import kotlinx.coroutines.*
import java.awt.*
import javax.swing.JFileChooser
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.filechooser.FileNameExtensionFilter

class MainScreen : JFrame("Chippo Eighto"), Display {
    val viewModel = HomeViewModel(this)
    private var screenBuffer = Array<ULong>(32) { 0u }
    private var canvas: JPanel? = null

    init {
        createUI(title)
    }

    private fun createUI(title: String) {

        setTitle(title)

        defaultCloseOperation = EXIT_ON_CLOSE
        setSize(400, 300)
        setLocationRelativeTo(null)
    }



    companion object {
        public fun createAndShowGUI() {

            val frame = MainScreen()
            frame.isVisible = true

            val menu = Menu("Chip8")

            menu.add(MenuItem("Load").apply {
                name = "Load ROM"
                addActionListener {
                    val fileChooser = JFileChooser()
                    fileChooser.apply {
                        fileFilter = FileNameExtensionFilter("Chip8 ROMS (*.ch8, *.rom)", "ch8", "rom")
                        val returnValue = showOpenDialog(frame)
                        if (returnValue == JFileChooser.APPROVE_OPTION) {
                            GlobalScope.launch(Dispatchers.IO) {
                                val fileBytes = fileChooser.selectedFile.readBytes()
                                withContext(Dispatchers.Unconfined) {
                                    frame.viewModel.loadRom(fileBytes)
                                }
                            }
                        }
                    }
                }
            })

            menu.name = "Chip8"

            val menuBar = MenuBar()
            menuBar.apply {
                add(menu)
            }

            frame.menuBar = menuBar
            frame.addKeyListener(frame.viewModel.keyboard)
            frame.canvas = frame.MyPanel().apply {
                background = Color.BLUE
            }
            frame.add(frame.canvas)

        }
    }

    override fun draw(screenBuffer: Array<ULong>) {
        this.screenBuffer = screenBuffer
        revalidate()
        repaint()
    }

    inner class MyPanel(layout: LayoutManager? = null, isDoubleBuffered: Boolean = true) : JPanel(layout, isDoubleBuffered) {

        override fun paintComponent(g: Graphics?) {
            super.paintComponent(g)

            val sb = screenBuffer.copyOf()
            g?.color = Color.GREEN
            val cellWidth = this.width / 64
            val cellHeight = this.height / 32
            for (y in 0 until 32) {
                val ycoord = y * cellHeight
                val row = sb[y]
                for (x in 0 until 64) {
                    val xcoord = x * cellWidth
                    val cellExists = row and 1.toULong().rotateRight(x + 1)
                    if (cellExists != 0.toULong()) {
                        g?.fillRect(xcoord, ycoord, cellWidth, cellHeight)
                    }
                }
            }
        }

    }
}




fun main() {
    EventQueue.invokeLater(MainScreen::createAndShowGUI)
}