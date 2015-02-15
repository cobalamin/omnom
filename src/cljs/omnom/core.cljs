(ns omnom.core
  (:require [om.core :as om :include-macros true]
            [om-tools.dom :as dom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]))

(def app-state
  (atom
   {:notes [{:x 100
             :y 50
             :w 200
             :h 150
             :content "# This is cool\nTite *tite* __tite__\n```js\nfunction add(a, b) {\n  return a + b;\n}\n```"}
            {:x 200
             :y 300
             :w 400
             :h 200
             :content "[Gooooogle](http://google.com)"}
            {:x 500
             :y 20
             :w 200
             :h 150
             :content "# Head\n## lines\n### are cool"}]}))

(defn highlight!
  []
  (.initHighlightingOnLoad js/hljs))
(highlight!)

(defcomponent note-view
  [note owner]
  
  (render
   [_]
   (let [{:keys [x y w h content]} note]
     (dom/div {:class "note"

               :style
               {:left x :top y
                :width w :height h}

               :dangerouslySetInnerHTML {:__html (js/marked content)}})))

  (did-update
   [_ _ _]
   nil))

(defcomponent root-view
  [app owner]

  (render
   [_]
   (dom/div
    (om/build-all note-view (:notes app)))))

(defn main []
  (om/root
    root-view
    app-state
    {:target (. js/document (getElementById "app"))}))
