//
//  ContentView.swift
//  iosApp
//
//  Created by Yang D. Nash on 18.12.25.
//


import SwiftUI
import shared

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainViewControllerKt.mainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
